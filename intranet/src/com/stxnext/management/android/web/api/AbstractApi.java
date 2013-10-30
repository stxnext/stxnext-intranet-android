package com.stxnext.management.android.web.api;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.common.base.Strings;
import com.stxnext.management.android.storage.prefs.StoragePrefs;

public class AbstractApi {

    private static final String TAG = "AbstractApi";
    
    protected Application app;
    protected StoragePrefs prefs;

    private boolean connected;
    

    protected AbstractApi(Application app){
        this.app = app;
        prefs = StoragePrefs.getInstance(app);
        this.connected = checkConnetivityManagerConnectionAndUpdateServiceState();
    }
    
    public boolean getPreviousConnectionState() {
        return connected;
    }

    public void setCurrentConnectionState(boolean state) {
        this.connected = state;
    }

    public boolean isOnline() {
        return connected;
    }
    
    public boolean isUserSignedIn(){
        return prefs.getAuthCode()!=null;
    }
    
    public boolean checkConnetivityManagerConnectionAndUpdateServiceState() {
        ConnectivityManager cm = (ConnectivityManager) app
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            connected = false;
        }
        else {
            connected = ni.isConnected();
        }
        return this.connected;
    }

    protected <T> T call(boolean requiresAuth, ApiExecutable<T> exe) {
        T result = null;
        if (!isOnline())
            return null;

        try {
            result = exe.call();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }

        return result;
    }

    protected <T> T releaseResultOrReactToError(HTTPResponse<T> response) {
        T result = null;
        result = response.getExpectedResponse();

        if (response.getError() != null) {

        }

        return result;
    }


}
