
package com.stxnext.management.android.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.postmessage.AbsenceMessage;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;
import com.stxnext.management.android.dto.postmessage.LatenessMessage;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;
import com.stxnext.management.android.ui.fragment.AbsenceFormFragment;
import com.stxnext.management.android.ui.fragment.FormActionReceiver;
import com.stxnext.management.android.ui.fragment.FormFragmentAdapter;
import com.stxnext.management.android.ui.fragment.LatenessFormFragment;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;
import com.viewpagerindicator.PageIndicator;

public class SubmitFormActivity extends SherlockFragmentActivity implements FormActionReceiver {

    FormFragmentAdapter mAdapter;
    ExtendedViewPager mPager;
    PageIndicator mIndicator;
    IntranetApi api;

    AbsenceFormFragment absenceFragment;
    LatenessFormFragment latenessFragment;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        api = IntranetApi.getInstance(getApplication());

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_forms);
        getSherlock().setProgressBarIndeterminateVisibility(true);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        absenceFragment = new AbsenceFormFragment();
        latenessFragment = new LatenessFormFragment();
        absenceFragment.setSherlock(getSherlock());
        latenessFragment.setSherlock(getSherlock());

        fragments.add(absenceFragment);
        fragments.add(latenessFragment);

        mAdapter = new FormFragmentAdapter(getSupportFragmentManager(), fragments);

        mPager = (ExtendedViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(3);
        mPager.setAdapter(mAdapter);

        mIndicator = (PageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }

    private void setFormsEnabled(boolean enabled) {
        absenceFragment.setFormEnabled(enabled);
        latenessFragment.setFormEnabled(enabled);
        mPager.setPagingEnabled(enabled);
    }

    @Override
    public void onSubmitFormWithMessage(AbstractMessage message) {
        new SubmitTask(message).execute();
    }

    private class SubmitTask extends AsyncTaskEx<Void, Void, HTTPResponse<Boolean>> {
        AbstractMessage message;

        public SubmitTask(AbstractMessage message) {
            this.message = message;
        }

        @Override
        protected void onPreExecute() {
            getSherlock().setProgressBarIndeterminateVisibility(true);
            setFormsEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected HTTPResponse<Boolean> doInBackground(Void... params) {
            if (message instanceof LatenessMessage) {
                return api.submitLateness((LatenessMessage) message);
            }
            else if (message instanceof AbsenceMessage) {
                return api.submitAbsence((AbsenceMessage) message);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HTTPResponse<Boolean> result) {
            // please, move to loaders instead of AsyncTask or just use
            // robospice library
            if (isFinishing())
                return;

            if (result.ok()) {
                setResult(RESULT_OK);
                finish();
            }
            else {
                getSherlock().setProgressBarIndeterminateVisibility(false);
                setFormsEnabled(true);
                Toast.makeText(SubmitFormActivity.this,
                        "Błąd wysyłania formularza. Spróbuj ponownie.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }
}
