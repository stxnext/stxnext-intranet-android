
package com.stxnext.management.android.ui;

import java.util.List;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
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
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
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
    ListView remoteWorkList;
    ListView outOfOfficeList;

    Cursor usersCursor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("","on search key presssed");
                return false;
            }
        });
        
        searchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("","on search close");
                return false;
            }
        });
        
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("","onQueryTextSubmit");
                return false;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null){
                    searchQuery = new String(newText);
                    Cursor dbEntities = DAO.getInstance().getIntranetUser()
                            .fetchFilteredCursor(searchQuery);
                    fillListWithData(dbEntities, false);
                }
                return false;
            }
        });
      
        return true;
    }

    String searchQuery = null;

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Cursor dbEntities = DAO.getInstance().getIntranetUser()
                    .fetchFilteredCursor(searchQuery);
            fillListWithData(dbEntities, false);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchQuery != null) {
            Cursor dbEntities = DAO.getInstance().getIntranetUser()
                    .fetchFilteredCursor(null);
            fillListWithData(dbEntities, true);
            searchQuery = null;
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void fillViews() {
        ptrListViewWrapper = (PullToRefreshListView) findViewById(R.id.listView);
        progressView = (ViewGroup) findViewById(R.id.progressView);
        progressText = (TextView) findViewById(R.id.progressText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userList = ptrListViewWrapper.getRefreshableView();
        userList.setFastScrollAlwaysVisible(true);
        
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawer = (ViewGroup) findViewById(R.id.right_drawer);

        remoteWorkList = (ListView) findViewById(R.id.remoteWorkList);
        outOfOfficeList = (ListView) findViewById(R.id.oooList);
        
        absenceList = (ListView) findViewById(R.id.absenceList);
        //latenessList = (ListView) findViewById(R.id.latenessList);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        
        remoteWorkList.setOnItemClickListener(latenessClickAdapter);
        outOfOfficeList.setOnItemClickListener(latenessClickAdapter);
    }
    
    private OnItemClickListener latenessClickAdapter = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Lateness item = (Lateness) parent.getAdapter().getItem(position);
            Toast.makeText(MainActivity.this, item.getExplanation(), Toast.LENGTH_SHORT).show();
        }
    };

    private boolean reloadingPullToRefresh;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!isUserSignedIn()){
            menu.removeItem(R.id.action_signout);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_absences) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else if (item.getItemId() == R.id.action_late) {
            drawerLayout.openDrawer(GravityCompat.END);
        }
        else if(item.getItemId() == R.id.action_signout){
            displayDialogBox("Logowanie", "Czy na pewno chcesz się wylogować?",signOutAction );
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private Runnable signOutAction = new Runnable() {
        public void run() {
            api.signOut();
            onSignInAction();
        }
    };
    
    @Override
    protected void setActions() {

        ptrListViewWrapper.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                reloadingPullToRefresh = true;
                if (api.isOnline()) {
                    new LoadUsersTask(true, false).execute();
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

                IntranetUser user = (IntranetUser) adapter.getItem(position - 1);
                Log.e("", "tapped user: " + user.getName() + " at position:" + position);
                Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                intent.putExtra(UserDetailsActivity.EXTRA_USER, user);
                startActivity(intent);
            }
        });

        if (!isUserSignedIn()) {
            onSignInAction();
        }
        else {
            loadData();
        }

    }
    
    private void onSignInAction(){
        startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
        new PreloadDataTask(false).execute();
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

    private void loadData() {
        new PreloadDataTask(true).execute();
    }

    //
    // @Override
    // protected void onResume() {
    // super.onResume();
    // new LoadUsersTask(false, false).execute();
    // }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_main;
    }

    private void setViewLoading(boolean loading) {
        ptrListViewWrapper.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        progressText.setText("Wczytuję...");
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);

    }

    private void setNoResults() {
        ptrListViewWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressText.setText("Brak wyników");
        progressView.setVisibility(View.VISIBLE);
    }

    private void fillListWithData(Cursor c, boolean animated) {

        if (c.getCount() > 0) {
            if (adapter == null) {
                usersCursor = c;
                startManagingCursor(c);
                adapter = new UserListAdapter(this, c, userList);
                userList.setAdapter(adapter);
            }
            else {
                stopManagingCursor(usersCursor);
                startManagingCursor(c);
                usersCursor = c;
                adapter.changeCursor(c);
                adapter.notifyDataSetChanged();
            }
            if (animated)
                applyListAnimation(userList);
            else
                setViewLoading(false);
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
            loadData();
        }
    }

    private AbsenceListAdapter absenceAdapter;
    private LatenessListAdapter outOfOfficeAdapter;
    private LatenessListAdapter workFromHomeAdapter;

    private class PreloadDataTask extends AsyncTaskEx<Void, Void, Void> {

        List<Absence> dbAbsences;
        List<Lateness> dbOutOfOffice;
        List<Lateness> dbWorkFromHome;
        Cursor dbEntities;
        boolean anythingInDb;
        
        private boolean completeWithAPIFetch;

        PreloadDataTask(boolean completeWithAPIFetch) {
            this.completeWithAPIFetch = completeWithAPIFetch;
            anythingInDb = DAO.getInstance().getIntranetUser().getEntityCount()>0;
        }

        @Override
        protected void onPreExecute() {
            
            setViewLoading(!anythingInDb);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbAbsences = DAO.getInstance().getAbsence().fetch();
            dbOutOfOffice = DAO.getInstance().getLate().fetch(false);
            dbWorkFromHome = DAO.getInstance().getLate().fetch(true);
            dbEntities = DAO.getInstance().getIntranetUser().fetchFilteredCursor(null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            fillListWithData(dbEntities, completeWithAPIFetch);
            absenceAdapter = new AbsenceListAdapter(MainActivity.this, dbAbsences);
            
            outOfOfficeAdapter = new LatenessListAdapter(MainActivity.this, dbOutOfOffice);
            workFromHomeAdapter = new LatenessListAdapter(MainActivity.this, dbWorkFromHome);
            
            outOfOfficeList.setAdapter(outOfOfficeAdapter);
            remoteWorkList.setAdapter(workFromHomeAdapter);
            absenceList.setAdapter(absenceAdapter);
            
            setViewLoading(!anythingInDb);
            if (completeWithAPIFetch) {
                new LoadUsersTask(true, dbEntities.getCount() <= 0).execute();
            }
        }

    }

    private class LoadUsersTask extends AsyncTask<Void, Void, HTTPResponse<IntranetUsersResult>> {

        private boolean pullToRefreshMode;
        private boolean noDbData;

        public LoadUsersTask(boolean pullToRefreshMode, boolean noDbData) {
            this.pullToRefreshMode = pullToRefreshMode;
            this.noDbData = noDbData;
        }

        @Override
        protected void onPreExecute() {
            if (!pullToRefreshMode) {
                setViewLoading(noDbData);
            }
            super.onPreExecute();
        }

        @Override
        protected HTTPResponse<IntranetUsersResult> doInBackground(Void... params) {
            if (pullToRefreshMode) {
                if (adapter != null) {
                    adapter.clearCache();
                    //BitmapUtils.cleanTempDir(AppIntranet.getApp());
                }
            }
            HTTPResponse<IntranetUsersResult> result = api.getUsers();
            if (result != null && result.getExpectedResponse() != null) {
                DAO.getInstance().getIntranetUser().clear();
                DAO.getInstance().getIntranetUser()
                        .persist(result.getExpectedResponse().getUsers());
            }

            HTTPResponse<PresenceResult> presenceResult = api.getPresences();
            if (presenceResult != null && presenceResult.getExpectedResponse() != null) {
                
                DAO.getInstance().getAbsence().clear();
                DAO.getInstance().getLate().clear();
                
                DAO.getInstance().getAbsence()
                        .persist(presenceResult.getExpectedResponse().getAbsences());
                DAO.getInstance().getLate()
                        .persist(presenceResult.getExpectedResponse().getLates());
            }

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
                new PreloadDataTask(false).execute();
            }
        }
    }

}
