package com.stxnext.management.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.common.base.Preconditions;

public class CommandReceiver extends BroadcastReceiver {

    public static final String ACTION_ACTIVITY_COMMAND = "com.stxnext.management.android.receivers.ACTIVITY_ACTION";

    public static final int EVENT_ONLINE = 1;
    public static final int EVENT_OFFLINE = 2;
    public static final int EVENT_LOST_SESSION = 3;
    public static final int EVENT_STARTED_SYNC = 4;
    public static final int EVENT_FINISHED_SYNC = 5;
    public static final String EXTRA_EVENT_TYPE = "eventType";

    private CommandReceiverListener listener;

    public CommandReceiver(CommandReceiverListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        validateInput(extras);

        int eventType = extras.getInt(EXTRA_EVENT_TYPE);
        if (eventType == EVENT_ONLINE) {
            this.listener.onOnline();
        }
        if (eventType == EVENT_OFFLINE) {
            this.listener.onOffline();
        }
        if (eventType == EVENT_LOST_SESSION) {
            this.listener.onLostSession();
        }
        if(eventType == EVENT_STARTED_SYNC){
            this.listener.onSyncStateChanged(true);
        }
        if(eventType == EVENT_FINISHED_SYNC){
            this.listener.onSyncStateChanged(false);
        }
    }

    private void validateInput(Bundle extras) {
        Preconditions.checkArgument(extras != null,
                "no extras provided");
        Preconditions.checkArgument(extras.containsKey(EXTRA_EVENT_TYPE),
                "event type is required");
    }

    public interface CommandReceiverListener {
        public void onOffline();
        public void onOnline();
        public void onLostSession();
        public void onSyncStateChanged(boolean started);
    }

}
