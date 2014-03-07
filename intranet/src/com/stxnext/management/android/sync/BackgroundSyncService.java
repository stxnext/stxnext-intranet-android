
package com.stxnext.management.android.sync;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.receivers.CommandReceiver;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.storage.sqlite.dao.DAO;

public class BackgroundSyncService extends IntentService {

    public static final String ACTION = "com.stxnext.management.android.sync.BackgroundSyncService.ACTION";
    public static final int ACTION_SYNC = 2;

    StoragePrefs prefs;
    Handler toastHandler;
//
//    public BackgroundSyncService(String name) {
//        super(name);
//        prefs = StoragePrefs.getInstance(this);
//        toastHandler = new Handler();
//    }
    
    public BackgroundSyncService() {
        super(BackgroundSyncService.class.getName());
        prefs = StoragePrefs.getInstance(this);
        toastHandler = new Handler();
    }

    private void toastMessage(final String message) {
        toastHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int action = intent.getIntExtra(ACTION, 0);
        // nope. This is not Yoda speaking. This is the easy way to avoid null
        // exceptions if action would be null (it could be)
        if (action != ACTION_SYNC)
            return;

        setFinished(false);

        List<IntranetUser> users = DAO.getInstance().getIntranetUser().fetchFiltered();
        ContactSyncManager manager = new ContactSyncManager(this);

        for (IntranetUser user : users) {
            if (!Strings.isNullOrEmpty(user.getPhone())) {
                List<ProviderPhone> providerPhones = manager.query(user.getPhone(), user.getName());
                if(providerPhones.size()<=0){
                    ProviderPhone providerPhone = new ProviderPhone();
                    providerPhone.setDisplayName(user.getName());
                    providerPhone.setNumberToUpdate(user.getPhone());
                    providerPhones.add(providerPhone);
                }
                else{
                    for(ProviderPhone existing : providerPhones){
                        existing.setNumberToUpdate(user.getPhone());
                    }
                }
                manager.mergeContacts(providerPhones, user);
            }
        }

        setFinished(true);
    }

    private void setFinished(boolean finished) {
        int toastResource = finished?R.string.notification_sync_finished:R.string.notification_sync_started;
        String msg = getString(toastResource);
        toastMessage(msg);

        prefs.setSyncing(!finished);
        int event = finished ? CommandReceiver.EVENT_FINISHED_SYNC
                : CommandReceiver.EVENT_STARTED_SYNC;
        Intent i = new Intent(CommandReceiver.ACTION_ACTIVITY_COMMAND);
        i.putExtra(CommandReceiver.EXTRA_EVENT_TYPE,
                event);
        this.getApplication().sendBroadcast(i);
    }

}
