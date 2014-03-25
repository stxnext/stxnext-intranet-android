
package com.stxnext.management.android.games.poker;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.games.poker.multiplayer.NIOConnectionHandler;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.ExtendedViewPager;

public class SetupActivity extends SherlockFragmentActivity implements GameSetupListener {

    private StoragePrefs prefs;

    SetupFragmentAdapter mAdapter;
    ExtendedViewPager mPager;
    SetupRoleFragment roleFragment;
    SetupSessionFragment sessionFragment;
    JoinSessionFragment joinFragment;
    SessionPreviewFragment sessionPreviewFragment;
    FragmentManager fragmentManager;

    IntranetUser currentUser;
    List<Team> teams;

    private static final int POSITION_SESSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_game_setup);

        GameData.getInstance().clear();
        NIOConnectionHandler.getInstance().stop();

        prefs = StoragePrefs.getInstance(this);

        roleFragment = new SetupRoleFragment(this);
        fragmentManager = getSupportFragmentManager();
        mAdapter = new SetupFragmentAdapter(this, fragmentManager);
        mAdapter.addFragment(roleFragment);
        mPager = (ExtendedViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(10);
        mPager.setAdapter(mAdapter);

        mPager.setPagingEnabled(false);
        new LoadDataTask().execute();
    }
    
    @Override
    public void onRoleChosen(GameRole role) {

        if (GameRole.MASTER.equals(role)) {
            sessionFragment = new SetupSessionFragment(this);
            sessionFragment.setFormEnabled(true);
            mAdapter.addFragment(sessionFragment);
            sessionPreviewFragment = new SessionPreviewFragment(this);
            mAdapter.addFragment(sessionPreviewFragment);
        }
        else if (GameRole.PARTICIPANT.equals(role)) {
            joinFragment = new JoinSessionFragment(this);
            joinFragment.setFormEnabled(true);
            mAdapter.addFragment(joinFragment);
        }
        mPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(POSITION_SESSION, true);
            }
        },500);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() > 1) {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            return;
        }
        else if (mPager.getCurrentItem() == 1) {
            Builder builder = new android.app.AlertDialog.Builder(this)
                    .setTitle("Discard session setup?")
                    .setMessage("Do you wish to discard session setup?")
                    .setNegativeButton(getString(R.string.common_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .setPositiveButton(getString(R.string.common_yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
            builder.show();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public IntranetUser getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public List<Team> getTeams() {
        return teams;
    }

    public interface SetupActivityListener {
        public void setFormEnabled(boolean enabled);
    }

    private void setLoadingData(boolean loading) {
        getSherlock().setProgressBarIndeterminateVisibility(loading);
        roleFragment.setFormEnabled(!loading);
        //sessionFragment.setFormEnabled(!loading);
    }

    private class LoadDataTask extends AsyncTaskEx<Void, Void, Void> {

        Handler handler;

        LoadDataTask() {
            handler = new Handler();
        }

        @Override
        protected void onPreExecute() {
            setLoadingData(true);
            super.onPreExecute();
        }

        @Override
        protected void onCancelled() {
            // this shouldn't be cancelled
            Toast.makeText(SetupActivity.this, "Couldn't load basic data", Toast.LENGTH_SHORT)
                    .show();
            finish();
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Long userId = prefs.getCurrentUserId();
            if (userId == null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetupActivity.this, "Couldn't identify user",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            }
            currentUser = DAO.getInstance().getIntranetUser().getById(userId);
            teams = DAO.getInstance().getTeam().fetch();
            GameData.getInstance().setCurrentUser(currentUser);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (isFinishing())
                return;

            setLoadingData(false);

        }
    }

    @Override
    public SetupFragmentAdapter getFragmentAdapter() {
        return mAdapter;
    }

    @Override
    public ExtendedViewPager getViewPager() {
        return mPager;
    }

}
