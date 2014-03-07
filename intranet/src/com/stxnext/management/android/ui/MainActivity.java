
package com.stxnext.management.android.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnCloseListener;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
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
import com.stxnext.management.android.receivers.CommandReceiver;
import com.stxnext.management.android.receivers.CommandReceiver.CommandReceiverListener;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.sync.BackgroundSyncService;
import com.stxnext.management.android.ui.dependencies.AbsenceListAdapter;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.LatenessListAdapter;
import com.stxnext.management.android.ui.dependencies.UserListAdapter;
import com.stxnext.management.android.web.api.HTTPResponse;

public class MainActivity extends AbstractSimpleActivity implements
        CommandReceiverListener {

    private static int REQUEST_LOGIN = 2;
    ListView userList;
    PullToRefreshListView ptrListViewWrapper;
    ViewGroup progressView;
    UserListAdapter adapter;
    TextView progressText;
    ProgressBar progressBar;
    DrawerLayout drawerLayout;
    View noresultView;
    Button reloadButton;

    ViewGroup leftDrawer;
    ViewGroup rightDrawer;

    ListView absenceList;
    ListView remoteWorkList;
    ListView outOfOfficeList;
    SearchView searchView;
    SearchManager searchManager;

    Cursor usersCursor;
    private CommandReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.receiver = new CommandReceiver(this);
        registerReceiver(receiver, new IntentFilter(
                CommandReceiver.ACTION_ACTIVITY_COMMAND));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    // @Override
    // public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu)
    // {
    // return true;
    // }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getSupportMenuInflater().inflate(R.menu.main, menu);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));

        searchView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        searchView.setOnCloseListener(new OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    setSearchQuery(new String(newText));
                    // Cursor dbEntities = DAO.getInstance().getIntranetUser()
                    // .fetchFilteredCursor(searchQuery);
                    // fillListWithData(dbEntities, false);
                    updateList();
                }
                return false;
            }
        });

        if (!isUserSignedIn()) {
            menu.removeItem(R.id.action_late);
            menu.removeItem(R.id.action_absences);
            menu.removeItem(R.id.action_signout);
        } else {
            menu.removeItem(R.id.action_signin);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (searchQuery != null) {
            setSearchQuery(null);
            updateList();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.common_exit_app)
                    .setMessage(R.string.notification_confirm_exit)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }
    }

    private void setSearchQuery(String query) {
        if (adapter != null) {
            adapter.setDontUseCursorCache(query == null ? false : true);
        }
        searchQuery = query;
    }

    String searchQuery = null;

    private void updateList() {
        Cursor dbEntities = DAO.getInstance().getIntranetUser()
                .fetchFilteredCursor(searchQuery);
        fillListWithData(dbEntities, false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            // Cursor dbEntities = DAO.getInstance().getIntranetUser()
            // .fetchFilteredCursor(searchQuery);
            // fillListWithData(dbEntities, false);
            updateList();
        }
    }

    @Override
    protected void fillViews() {
        ptrListViewWrapper = (PullToRefreshListView) findViewById(R.id.listView);
        progressView = (ViewGroup) findViewById(R.id.progressView);
        progressText = (TextView) findViewById(R.id.progressText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        userList = ptrListViewWrapper.getRefreshableView();
        if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            userList.setFastScrollAlwaysVisible(true);
        }
        userList.setFastScrollEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (ViewGroup) findViewById(R.id.left_drawer);
        rightDrawer = (ViewGroup) findViewById(R.id.right_drawer);

        remoteWorkList = (ListView) findViewById(R.id.remoteWorkList);
        outOfOfficeList = (ListView) findViewById(R.id.oooList);

        absenceList = (ListView) findViewById(R.id.absenceList);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        noresultView = findViewById(R.id.noresultView);
        reloadButton = (Button) findViewById(R.id.reloadButton);

        remoteWorkList.setOnItemClickListener(latenessClickAdapter);
        outOfOfficeList.setOnItemClickListener(latenessClickAdapter);
        absenceList.setOnItemClickListener(absenceClickAdapter);
    }

    private OnItemClickListener latenessClickAdapter = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Lateness item = (Lateness) parent.getAdapter().getItem(position);
            Toast.makeText(MainActivity.this, item.getExplanation(),
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    private OnItemClickListener absenceClickAdapter = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Absence item = (Absence) parent.getAdapter().getItem(position);
            Toast.makeText(MainActivity.this, item.getRemarks(),
                    Toast.LENGTH_SHORT).show();
        }
    };

    private boolean reloadingPullToRefresh;

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_absences) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.action_late) {
            drawerLayout.openDrawer(GravityCompat.END);
        } else if (item.getItemId() == R.id.action_signout) {
            displayDialogBox(getString(R.string.common_signing_in), getString(R.string.notification_confirm_signout),
                    signOutAction);
        } else if (item.getItemId() == R.id.action_signin) {
            onSignInAction();
        } else if (item.getItemId() == R.id.sync_all) {
            if (prefs.isSyncing()) {
                Toast.makeText(this, getString(R.string.notification_sync_in_progress),
                        Toast.LENGTH_SHORT).show();
            } else {
                displayDialogBox(
                        getString(R.string.common_sync_contacts),
                        getString(R.string.notification_sync_confirmation),
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent intentContinueLoading = new Intent(
                                        getApplicationContext(),
                                        BackgroundSyncService.class);
                                intentContinueLoading.putExtra(
                                        BackgroundSyncService.ACTION,
                                        BackgroundSyncService.ACTION_SYNC);
                                getApplicationContext().startService(
                                        intentContinueLoading);
                            }
                        });
            }
        } else if (item.getItemId() == R.id.addform) {
            startActivityForResult(new Intent(this, SubmitFormActivity.class),
                    SubmitFormActivity.REQUEST_SEND_FORM);
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

        ptrListViewWrapper
                .setOnRefreshListener(new OnRefreshListener2<ListView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        reloadingPullToRefresh = true;
                        if (api.isOnline()) {
                            new LoadUsersTask(true, false).execute();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.common_no_connection), Toast.LENGTH_SHORT)
                                    .show();
                            ptrListViewWrapper.onRefreshComplete();
                            reloadingPullToRefresh = false;
                        }
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                    }
                });

        userList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                IntranetUser user = (IntranetUser) adapter
                        .getItem(position - 1);
                Intent intent = new Intent(MainActivity.this,
                        UserDetailsActivity.class);
                intent.putExtra(UserDetailsActivity.EXTRA_USER, user);
                startActivity(intent);
            }
        });

        if (!isUserSignedIn()) {
            onSignInAction();
        } else {
            loadData();
        }

    }

    private boolean launchedSignin;

    private void onSignInAction() {
        if (!launchedSignin) {
            startActivityForResult(new Intent(this, LoginActivity.class),
                    REQUEST_LOGIN);
            launchedSignin = true;
        }
        new PreloadDataTask(false).execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            launchedSignin = false;
            if (resultCode == LoginActivity.RESULT_SIGNED_IN) {
                new AuthUserTask().execute();
            } else if (resultCode == LoginActivity.RESULT_CANCELLED) {
                finish();
            }
        }
        else if (requestCode == SubmitFormActivity.REQUEST_SEND_FORM) {
            if (resultCode == RESULT_OK) {
                reloadingPullToRefresh = true;
                if (api.isOnline()) {
                    new LoadUsersTask(true, false).execute();
                }
            }
        }
    }

    private void loadData() {
        new PreloadDataTask(true).execute();
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_main;
    }

    private void setViewLoading(boolean loading) {
        ptrListViewWrapper.setVisibility(loading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        progressText.setText(R.string.label_loading);
        progressView.setVisibility(loading ? View.VISIBLE : View.GONE);

    }

    private void setNoResults() {
        ptrListViewWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        progressText.setText(R.string.label_no_results);
        progressView.setVisibility(View.VISIBLE);
    }

    private void fillListWithData(Cursor c, boolean animated) {

        if (c.getCount() > 0) {
            if (adapter == null) {
                usersCursor = c;
                // use loaders next time
                startManagingCursor(c);
                adapter = new UserListAdapter(this, c, userList);
                userList.setAdapter(adapter);
            } else {
                stopManagingCursor(usersCursor);
                startManagingCursor(c);
                usersCursor = c;
                adapter.changeCursor(c);
                adapter.notifyDataSetChanged();
                userList.invalidateViews();
            }
            if (animated)
                applyListAnimation(userList);
            else
                setViewLoading(false);
        } else {
            setNoResults();
        }
    }

    private class AuthUserTask extends AsyncTask<Void, Void, HTTPResponse<String>> {

        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }

        @Override
        protected HTTPResponse<String> doInBackground(Void... params) {
            return api.loginWithCode(prefs.getAuthCode());
        }

        @Override
        protected void onPostExecute(HTTPResponse<String> result) {
            super.onPostExecute(result);
            if (result.ok()) {
                loadData();
            }
            else {
                startActivity(new Intent(MainActivity.this, PresentationActivity.class));
                finish();
            }
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
            anythingInDb = DAO.getInstance().getIntranetUser().getEntityCount() > 0;
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
            dbEntities = DAO.getInstance().getIntranetUser()
                    .fetchFilteredCursor(null);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            fillListWithData(dbEntities, completeWithAPIFetch);
            absenceAdapter = new AbsenceListAdapter(MainActivity.this,
                    dbAbsences);

            outOfOfficeAdapter = new LatenessListAdapter(MainActivity.this,
                    dbOutOfOffice);
            workFromHomeAdapter = new LatenessListAdapter(MainActivity.this,
                    dbWorkFromHome);

            outOfOfficeList.setAdapter(outOfOfficeAdapter);
            remoteWorkList.setAdapter(workFromHomeAdapter);
            absenceList.setAdapter(absenceAdapter);

            setViewLoading(!anythingInDb);
            if (completeWithAPIFetch) {
                new LoadUsersTask(true, dbEntities.getCount() <= 0).execute();
            }
        }

    }

    private class LoadUsersTask extends
            AsyncTask<Void, Void, HTTPResponse<IntranetUsersResult>> {

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
        protected HTTPResponse<IntranetUsersResult> doInBackground(
                Void... params) {
            if (reloadingPullToRefresh) {
                if (adapter != null) {
                    BitmapUtils.cleanTempDir(AppIntranet.getApp());
                    adapter.clearCache();
                }
            }
            HTTPResponse<IntranetUsersResult> result = api.getUsers();
            if (result != null && result.getExpectedResponse() != null) {
                DAO.getInstance().getIntranetUser().clear();
                DAO.getInstance().getIntranetUser()
                        .persist(result.getExpectedResponse().getUsers());
            }

            HTTPResponse<PresenceResult> presenceResult = api.getPresences();
            if (presenceResult != null
                    && presenceResult.getExpectedResponse() != null) {

                DAO.getInstance().getAbsence().clear();
                DAO.getInstance().getLate().clear();

                DAO.getInstance()
                        .getAbsence()
                        .persist(
                                presenceResult.getExpectedResponse()
                                        .getAbsences());
                DAO.getInstance()
                        .getLate()
                        .persist(
                                presenceResult.getExpectedResponse().getLates());
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
                if (!launchedSignin) {
                    startActivityForResult(new Intent(MainActivity.this,
                            LoginActivity.class), REQUEST_LOGIN);
                    launchedSignin = true;
                }
            } else {
                new PreloadDataTask(false).execute();
            }
        }
    }

    @Override
    public void onOffline() {
        // Toast.makeText(this, "onOffline", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOnline() {
        // Toast.makeText(this, "onOnline", Toast.LENGTH_SHORT).show();
        if (!isUserSignedIn()) {
            onSignInAction();
        } else {
            loadData();
        }
    }

    @Override
    public void onLostSession() {
        api.signOut();
        onSignInAction();
        // Toast.makeText(this, "onLostSession", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSyncStateChanged(boolean started) {
        // TODO Auto-generated method stub

    }

}
