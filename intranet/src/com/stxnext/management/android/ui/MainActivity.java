
package com.stxnext.management.android.ui;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.Absence;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.dto.local.Lateness;
import com.stxnext.management.android.dto.local.PresenceResult;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.AbsenceListAdapter;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.LatenessListAdapter;
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
    DrawerLayout drawerLayout;
    
    ViewGroup leftDrawer;
    ViewGroup rightDrawer;
    
    ListView absenceList;
    ListView latenessList;
    private String[] titles;

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
        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawer = (ViewGroup) findViewById(R.id.right_drawer);
        
        absenceList = (ListView) findViewById(R.id.absenceList);
        latenessList = (ListView) findViewById(R.id.latenessList);
        
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
//        absenceList.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Absence item = (Absence) absenceAdapter.getItem(position);
//                Toast.makeText(MainActivity.this, item.get, Toast.LENGTH_SHORT).
//            }
//        });
        
        latenessList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Lateness item = (Lateness) latenessAdapter.getItem(position);
                Toast.makeText(MainActivity.this, item.getExplanation(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean reloadingPullToRefresh;

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if(item.getItemId() == R.id.action_absences){
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if(item.getItemId() == R.id.action_late){
            drawerLayout.openDrawer(GravityCompat.END);
        }
        return super.onMenuItemSelected(featureId, item);
    }
    
    @Override
    protected void setActions() {
        

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
        
        userList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                IntranetUser user = (IntranetUser) adapter.getItem(position-1);
                Log.e("","tapped user: "+user.getName()+" at position:"+position);
                Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                intent.putExtra(UserDetailsActivity.EXTRA_USER, user);
                startActivity(intent);
            }
        });
        
        if (!isUserSignedIn()) {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return;
        }

        new LoadUsersTask(false).execute();
        new LoadPresencesTask().execute();
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

    private AbsenceListAdapter absenceAdapter;
    private LatenessListAdapter latenessAdapter;
    
    private class LoadPresencesTask extends AsyncTask<Void, Void, HTTPResponse<PresenceResult>>{

        @Override
        protected HTTPResponse<PresenceResult> doInBackground(Void... params) {
            return api.getPresences();
        }
        
        @Override
        protected void onPostExecute(HTTPResponse<PresenceResult> result) {
            super.onPostExecute(result);
            if(result != null && result.getExpectedResponse()!=null){
                absenceAdapter = new AbsenceListAdapter(MainActivity.this, result.getExpectedResponse().getAbsences());
                latenessAdapter = new LatenessListAdapter(MainActivity.this, result.getExpectedResponse().getLates());
                
                absenceList.setAdapter(absenceAdapter);
                latenessList.setAdapter(latenessAdapter);
            }
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
            HTTPResponse<IntranetUsersResult> result = api.getUsers();
            if(result != null && result.getExpectedResponse()!=null){
                DAO.getInstance().getIntranetUser().persist(result.getExpectedResponse().getUsers());
            }
            
            List<IntranetUser> dbEntities = DAO.getInstance().getIntranetUser().fetch();
            
            return result;
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
                api.clearCookies();
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class),
                        REQUEST_LOGIN);
            }
            else {
                fillListWithData(result.getExpectedResponse());
            }
        }
    }
    
}
