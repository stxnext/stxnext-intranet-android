
package com.stxnext.management.android.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.UserListAdapter;
import com.stxnext.management.android.web.api.HTTPResponse;

public class MainActivity extends AbstractSimpleActivity {

    private static int REQUEST_LOGIN = 2;
    ListView userList;
    PullToRefreshListView ptrListViewWrapper;
    ViewGroup progressView;
    UserListAdapter adapter;
    TextView progressText;
    ProgressBar progressBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void fillViews() {
        ptrListViewWrapper = (PullToRefreshListView) findViewById(R.id.listView);
        progressView = (ViewGroup) findViewById(R.id.progressView);
        progressText = (TextView) findViewById(R.id.progressText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userList = ptrListViewWrapper.getRefreshableView();
    }

    private boolean reloadingPullToRefresh;

    @Override
    protected void setActions() {
        if (!isUserSignedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return;
        }

        ptrListViewWrapper.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                reloadingPullToRefresh = true;
                if (api.isOnline()) {
                    new LoadUsersTask(true).execute();
                } else {
                    Toast.makeText(MainActivity.this, "Unable to refresh in offline mode",
                            Toast.LENGTH_SHORT).show();
                    if (reloadingPullToRefresh) {
                        ptrListViewWrapper.onRefreshComplete();
                        reloadingPullToRefresh = false;
                    }
                }
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
            }
        });

        new LoadUsersTask(false).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == LoginActivity.RESULT_SIGNED_IN) {
                new AuthUserTask().execute();
            }
            else if (resultCode == LoginActivity.RESULT_CANCELLED) {
                finish();
            }
        }
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_main;
    }

    private void setViewLoading(boolean loading) {
        ptrListViewWrapper.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        progressText.setText("Wczytuj«...");
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);

    }

    private void setNoResults() {
        ptrListViewWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressText.setText("Brak wynik—w");
        progressView.setVisibility(View.VISIBLE);
    }

    private void fillListWithData(IntranetUsersResult results) {
        if (results != null && results.getUsers().size() > 0) {
            if(adapter==null){
                adapter = new UserListAdapter(this, userList, results.getUsers());
                userList.setAdapter(adapter);
            }
            else{
                adapter.setUsers(results.getUsers());
                adapter.notifyDataSetChanged();
            }
            
            applyListAnimation(userList);
        }
        else {
            setNoResults();
        }
    }

    private void applyListAnimation(ViewGroup view) {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(350);
        set.addAnimation(animation);
        animation = new AlphaAnimation(0.1f, 1.1f);
        animation.setDuration(80);
        animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
        view.setLayoutAnimation(controller);
    }

    private class AuthUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            api.loginWithCode(prefs.getAuthCode());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new LoadUsersTask(false).execute();
        }
    }

    private class LoadUsersTask extends AsyncTask<Void, Void, HTTPResponse<IntranetUsersResult>> {

        private boolean pullToRefreshMode;

        public LoadUsersTask(boolean pullToRefreshMode) {
            this.pullToRefreshMode = pullToRefreshMode;
        }

        @Override
        protected void onPreExecute() {
            if (!pullToRefreshMode) {
                setViewLoading(true);
            }
            super.onPreExecute();
        }

        @Override
        protected HTTPResponse<IntranetUsersResult> doInBackground(Void... params) {
            if(pullToRefreshMode){
                if(adapter!=null){
                    adapter.clearCache();
                    BitmapUtils.cleanTempDir(AppIntranet.getApp());
                }
            }
            return api.getUsers();
        }

        @Override
        protected void onPostExecute(HTTPResponse<IntranetUsersResult> result) {
            super.onPostExecute(result);
            if (isFinishing())
                return;

            setViewLoading(false);

            if (reloadingPullToRefresh) {
                ptrListViewWrapper.onRefreshComplete();
                reloadingPullToRefresh = false;
            }
            if (result == null) {
                prefs.setAuthCode(null);
                prefs.setCookies(null);
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),
                        REQUEST_LOGIN);
            }
            else {
                fillListWithData(result.getExpectedResponse());
            }
        }
    }
}
