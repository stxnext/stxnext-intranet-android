
package com.stxnext.management.android.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;

import com.stxnext.management.android.R;

public class MainActivity extends AbstractSimpleActivity {

    private static int REQUEST_LOGIN = 2;
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void fillViews() {
        
    }

    @Override
    protected void setActions() {
        // TODO Auto-generated method stub
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

    private class AuthUserTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            api.loginWithCode(prefs.getAuthCode());
            // TODO Auto-generated method stub
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new LoadUsersTask().execute();
        }
    }
    
    private class LoadUsersTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            api.getUsers();
            return null;
        }
    }
    
}
