
package com.stxnext.management.android.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.widget.ListView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.ui.dependencies.UserListAdapter;
import com.stxnext.management.android.web.api.HTTPResponse;

public class MainActivity extends AbstractSimpleActivity {

    private static int REQUEST_LOGIN = 2;
    ListView userList;
    UserListAdapter adapter;
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void fillViews() {
        userList = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void setActions() {
        if(!isUserSignedIn()){
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_LOGIN);
            return;
        }
        
        new LoadUsersTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGIN){
            if(resultCode == LoginActivity.RESULT_SIGNED_IN){
                new AuthUserTask().execute();
            }
            else if(resultCode == LoginActivity.RESULT_CANCELLED){
                finish();
            }
        }
    }
    
    
    @Override
    protected int getContentResourceId() {
        return R.layout.activity_main;
    }

    private void setViewLoading(boolean loading){
        
    }
    
    private void fillListWithData(IntranetUsersResult results){
        adapter = new UserListAdapter(this, results.getUsers());
        userList.setAdapter(adapter);
    }
    
    private class AuthUserTask extends AsyncTask<Void, Void, Void>{

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
            new LoadUsersTask().execute();
        }
    }
    
    private class LoadUsersTask extends AsyncTask<Void, Void, HTTPResponse<IntranetUsersResult>>{

        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }
        
        @Override
        protected HTTPResponse<IntranetUsersResult> doInBackground(Void... params) {
            return api.getUsers();
        }
        
        @Override
        protected void onPostExecute(HTTPResponse<IntranetUsersResult> result) {
            super.onPostExecute(result);
            fillListWithData(result.getExpectedResponse());
            setViewLoading(false);
        }
    }
}
