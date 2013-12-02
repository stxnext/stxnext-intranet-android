
package com.stxnext.management.android.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.AbsenceDisplayData;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.UserProperty;
import com.stxnext.management.android.ui.controls.RoundedImageView;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.PropertyListAdapter;

public class UserDetailsActivity extends AbstractSimpleActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_USER = "user";

    RoundedImageView userImageView;
    TextView nameView;
    ListView listView;
    TextView lateTimeView;
    TextView lateDescriptionView;
    PropertyListAdapter adapter;

    ViewGroup loadingView;
    ViewGroup loadedView;

    IntranetUser user;

    @Override
    protected void fillViews() {
        userImageView = (RoundedImageView) findViewById(R.id.userImageView);
        nameView = (TextView) findViewById(R.id.nameView);
        listView = (ListView) findViewById(R.id.listView);
        loadingView = (ViewGroup) findViewById(R.id.loadingView);
        loadedView = (ViewGroup) findViewById(R.id.loadedView);
        lateTimeView = (TextView) findViewById(R.id.lateTimeView);
        lateDescriptionView = (TextView) findViewById(R.id.lateDescriptionView);

        // userImageView.setCornersRadius(12F);

        Bundle bundle = getIntent().getExtras();
        user = (IntranetUser) bundle.getSerializable(EXTRA_USER);

        nameView.setText(user.getName());

        if (user.getAbsenceDisplayData() != null)
            insertAbsenceData(user.getAbsenceDisplayData(), lateTimeView,
                    lateDescriptionView);

        if (user.getLatenessDisplayData() != null)
            insertAbsenceData(user.getLatenessDisplayData(), lateTimeView,
                    lateDescriptionView);
        new LoadDataTask().execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_contact){
            addContactAction();
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    private void addContactAction(){
        getLoaderManager().initLoader(0, null, this);
        
    }

    private void insertAbsenceData(AbsenceDisplayData data,
            TextView lateTimeView, TextView lateDescriptionView) {
        if (data != null) {
            lateTimeView.setText(formatLatenessTime(data));
            lateDescriptionView
                    .setText(data.explanation);
            lateTimeView.setVisibility(View.VISIBLE);
            lateDescriptionView.setVisibility(View.VISIBLE);
        } else {
            lateTimeView.setVisibility(View.GONE);
            lateDescriptionView.setVisibility(View.GONE);
        }
    }

    private String formatLatenessTime(AbsenceDisplayData data) {
        if (data.start != null && data.end != null) {
            return data.start + " - " + data.end;
        }
        return null;
    }

    @Override
    protected void setActions() {

    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_user_details;
    }

    private void setViewLoading(boolean loading) {
        loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
        loadedView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private class LoadDataTask extends
            AsyncTaskEx<Void, Void, List<UserProperty>> {

        Bitmap bmp;

        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }

        @Override
        protected List<UserProperty> doInBackground(Void... params) {
            bmp = BitmapUtils.getTempBitmap(UserDetailsActivity.this, user
                    .getId().toString());
            return user.getProperties();
        }

        @Override
        protected void onPostExecute(List<UserProperty> result) {
            super.onPostExecute(result);
            if (isFinishing())
                return;

            if (bmp != null) {
                userImageView.setImageBitmap(bmp);
            }
            adapter = new PropertyListAdapter(UserDetailsActivity.this,
                    listView, result);
            listView.setAdapter(adapter);
            setViewLoading(false);
            applyListAnimation(listView);
        }

    }

    // Content provider related

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
    {
            Contacts._ID,
            Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY :
                    Contacts.DISPLAY_NAME

    };

    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    Contacts.DISPLAY_NAME + " LIKE ?";
    private String mSearchString;
    private String[] mSelectionArgs = {
        mSearchString
    };

    public Cursor mCursor;
    public int mLookupKeyIndex;
    public int mIdIndex;
    public String mCurrentLookupKey;
    public long mCurrentId;
    Uri mSelectedContactUri;

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        mSelectionArgs[0] = "%" + mSearchString + "%";
        // Starts the query
        return new CursorLoader(
                this,
                Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
        
    }

}
