
package com.stxnext.management.android.receivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.web.api.IntranetApi;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    Application app;
    IntranetApi api;

    public NetworkStateChangeReceiver() {
	app = AppIntranet.getApp();
	api = IntranetApi.getInstance(app);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	if (intent != null) {

            boolean noConnectivity = intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            boolean currentState = !noConnectivity;
            boolean previousState = api.getPreviousConnectionState();
            api.setCurrentConnectionState(currentState);

            boolean sendOffline = (previousState & !currentState);
            boolean sendOnline = (!previousState & currentState);

            if (sendOffline) {
                Intent i = new Intent(CommandReceiver.ACTION_ACTIVITY_COMMAND);
                i.putExtra(CommandReceiver.EXTRA_EVENT_TYPE,
            	    CommandReceiver.EVENT_OFFLINE);
                app.sendBroadcast(i);
            }
            else if (sendOnline) {
                Intent i = new Intent(CommandReceiver.ACTION_ACTIVITY_COMMAND);
                i.putExtra(CommandReceiver.EXTRA_EVENT_TYPE,
            	   CommandReceiver.EVENT_ONLINE);
                app.sendBroadcast(i);
            }
            
            
            
        }
    }
}
