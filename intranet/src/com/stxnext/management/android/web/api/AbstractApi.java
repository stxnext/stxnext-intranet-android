
package com.stxnext.management.android.web.api;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.stxnext.management.android.receivers.CommandReceiver;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.web.api.services.IntranetService;

public class AbstractApi {

    private static final String TAG = "AbstractApi";

    protected Application app;
    protected StoragePrefs prefs;
    protected IntranetService service;
    protected DAO dao;

    private boolean connected;

    protected AbstractApi(Application app) {
        this.app = app;
        prefs = StoragePrefs.getInstance(app);
        this.connected = checkConnetivityManagerConnectionAndUpdateServiceState();
        service = new IntranetService();
        dao = DAO.getInstance();
    }

    public void signOut() {
        prefs.setAuthCode(null);
        service.clearCookies();
        dao.clearAll();
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

    public boolean isUserSignedIn() {
        return prefs.getAuthCode() != null;
    }

    public boolean checkConnetivityManagerConnectionAndUpdateServiceState() {
        ConnectivityManager cm = (ConnectivityManager) app
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            connected = false;
        } else {
            connected = ni.isConnected();
        }
        return this.connected;
    }

    final Handler messageHandler = new Handler();

    protected <T> HTTPResponse<T> call(boolean requiresAuth,
            ApiExecutable<T> exe) {
        HTTPResponse<T> result = new HTTPResponse<T>();
        if(!isOnline()){
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(app, "Brak połączenia z siecią", Toast.LENGTH_SHORT).show();
                }
            });
            return result;
        }
        try {
            result = exe.call();
        } catch (Exception e) {
            if (!isOnline()) {
                result.setError(new HTTPError(800, "No network connection"));
            } else {
                if (e instanceof JsonSyntaxException) {
                    result.setError(new HTTPError(403, e.getMessage()));

                    Intent i = new Intent(CommandReceiver.ACTION_ACTIVITY_COMMAND);
                    i.putExtra(CommandReceiver.EXTRA_EVENT_TYPE,
                            CommandReceiver.EVENT_LOST_SESSION);
                    app.sendBroadcast(i);
                } else {
                    result.setError(new HTTPError(400, e.getMessage()));
                }

            }

            if (!result.ok() && result.getError().getCode() != 403) {
                final HTTPResponse<T> messageResult = result;
                messageHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(app, messageResult.getError().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
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
