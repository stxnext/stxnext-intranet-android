
package com.stxnext.management.android.sync;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.support.v4.app.LoaderManager;

import com.google.common.base.Strings;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;

public class ContactSyncManager implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String CONTACT_LABEL = "STXNext";

    public interface SyncManagerListener {
        public void onPhoneQueryComplete(List<ProviderPhone> phones);
    }

    private List<SyncManagerListener> listeners = new ArrayList<ContactSyncManager.SyncManagerListener>();
    private Context context;

    public void addListener(SyncManagerListener listeners) {
        this.listeners.add(listeners);
    }

    private  ContactSyncManager(){};
    public ContactSyncManager(IntentService service) {
        this.context = service;
    };

    public <T extends Context & SyncManagerListener> ContactSyncManager(T context) {
        this.context = context;
        this.listeners.add(context);
    }
    
    public boolean removeListener(SyncManagerListener listener) {
        boolean hasObject = this.listeners.contains(listener);
        if (hasObject)
            this.listeners.remove(listeners);
        return hasObject;
    }

    private void rollBackMultipleMerge(List<ProviderPhone> phones) {
        // TODO:
    }

    
    /**
     * This should be done only in thread if you want the app to be responsive
     * @param phones
     * @param user
     * @return
     */
    public boolean mergeContacts(List<ProviderPhone> phones, IntranetUser user) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        for (ProviderPhone phone : phones) {
            try {

                if (phone.getContactId() == null) {
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE,
                                    ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                            .withValue(ContactsContract.RawContacts.ACCOUNT_NAME,
                                    phone.getDisplayName())
                            .build());
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(
                                    ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    phone.getDisplayName())
                            .build());
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, user.getEmail())
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE,
                                    ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                            .build());
                    
                    ContentProviderResult[] result = context.getContentResolver().applyBatch(
                            ContactsContract.AUTHORITY, ops);
                    ops.clear();

                    Uri contactUri = result[0].uri;
                    String stringId = contactUri.getPathSegments().get(1);
                    Long id = Long.parseLong(stringId);
                    phone.setContactId(id);
                }
                //else {
                    ops.add(ContentProviderOperation
                            .newDelete(ContactsContract.Data.CONTENT_URI)
                            .withSelection(
                                    Phone.LABEL + " LIKE ? AND "
                                            + ContactsContract.Data.RAW_CONTACT_ID + "=?",
                                    new String[] {
                                            "%" + CONTACT_LABEL + " Mobile" + "%",
                                            String.valueOf(phone.getContactId().intValue())
                                    }).build());

                    ContentProviderOperation.Builder operation = ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValue(ContactsContract.Data.RAW_CONTACT_ID,
                                    phone.getContactId().intValue())
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    phone.getNumberToUpdate())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                            .withValue(Phone.LABEL, CONTACT_LABEL + " Mobile");

                    ops.add(operation.build());
                    
                    Bitmap bmp = BitmapUtils.getTempBitmap(context, user.getId().toString());
                    if(bmp!=null){
                        byte[] imageBytes = BitmapUtils.bitmapToBytes(bmp, CompressFormat.PNG);
                        
                        ContentValues values = new ContentValues();
                        values.put(ContactsContract.Data.RAW_CONTACT_ID, phone.getContactId().intValue()); 
                        values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1); 
                        values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, imageBytes); 
                        values.put(ContactsContract.Data.MIMETYPE, 
                                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE); 
                        context.getContentResolver().insert(
                                ContactsContract.Data.CONTENT_URI, 
                                values); 
                    }
                    
                    ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                //}

                    String b="";
            } catch (RemoteException e) {
                Log.e("", "SAVING ERROR", e);
            } catch (OperationApplicationException e) {
                Log.e("", "SAVING ERROR", e);
            }
        }
        return true;
    }

    boolean loaderInitialized;
    boolean activeQuery;

    public List<ProviderPhone> query(String phoneTerm, String nameTerm){
        SqlArgs args = resolveSqlParams(phoneTerm, nameTerm);
        Cursor c = context.getContentResolver().query(Phone.CONTENT_URI, PROJECTION, args.selection, args.params, null);
        return resolvePhonesFromCursor(c);
    }
    
    public void launchQueryAsync(LoaderManager loader, String phoneTerm, String nameTerm) {
        activeQuery = true;
        mNameSearchQuery = nameTerm;
        mPhoneSearchQuery = phoneTerm;

        if (!loaderInitialized) {
            loader.initLoader(0, null, this);
            loaderInitialized = true;
        }
        else {
            loader.restartLoader(0, null, this);
        }

    }

    private static final String PhoneNameColumn = Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.HONEYCOMB ?
            Phone.DISPLAY_NAME_PRIMARY :
            Phone.DISPLAY_NAME;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
    {
            Phone._ID,
            Phone.LOOKUP_KEY,
            Phone.RAW_CONTACT_ID,
            Phone.TYPE,
            PhoneNameColumn,
            Phone.NUMBER,
            Phone.LABEL
    };

    private String mNameSearchQuery;
    private String mPhoneSearchQuery;
    Uri mSelectedContactUri;

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        SqlArgs args = resolveSqlParams(mPhoneSearchQuery, mNameSearchQuery);
        return new CursorLoader(
                context,
                Phone.CONTENT_URI,
                PROJECTION,
                args.selection,
                args.params,
                null);
    }

    private SqlArgs resolveSqlParams(String phone, String name){
        List<String> selectionArgs = new ArrayList<String>();

        String SELECTION = "";
        if (!Strings.isNullOrEmpty(name)) {
            SELECTION += PhoneNameColumn + " LIKE ?";
            selectionArgs.add("%" + name + "%");
        }

        if (!Strings.isNullOrEmpty(phone)) {
            if (selectionArgs.size() > 0) {
                SELECTION = SELECTION + " OR ";
            }

            SELECTION += "(" + Phone.NUMBER + " LIKE ? AND " + Phone.LABEL + " LIKE ?)";
            selectionArgs.add("%" + phone + "%");
            selectionArgs.add("%" + CONTACT_LABEL + "%");
        }

        String[] array = new String[selectionArgs.size()];
        selectionArgs.toArray(array);
        
        SqlArgs args = new SqlArgs(SELECTION, array);
        return args;
    }
    
    List<ProviderPhone> resolvePhonesFromCursor(Cursor cursor){
        List<ProviderPhone> phones = new ArrayList<ProviderPhone>();
        if (cursor.getCount() > 0) {
            int columnName = cursor.getColumnIndex(PhoneNameColumn);
            int columnNumber = cursor.getColumnIndex(Phone.NUMBER);
            int columnContactId = cursor.getColumnIndex(Phone.RAW_CONTACT_ID);
            int columnType = cursor.getColumnIndex(Phone.TYPE);
            int columnId = cursor.getColumnIndex(Phone._ID);
            int columnLabel = cursor.getColumnIndex(Phone.LABEL);

            while (cursor.moveToNext()) {
                ProviderPhone phone = new ProviderPhone();
                if (!cursor.isNull(columnName))
                    phone.setDisplayName(cursor.getString(columnName));
                if (!cursor.isNull(columnNumber))
                    phone.setPhoneNumber(cursor.getString(columnNumber));
                if (!cursor.isNull(columnId))
                    phone.setId(cursor.getLong(columnId));
                if (!cursor.isNull(columnContactId))
                    phone.setContactId(cursor.getLong(columnContactId));
                if (!cursor.isNull(columnType))
                    phone.setType(cursor.getString(columnContactId));
                if (!cursor.isNull(columnLabel))
                    phone.setLabel(cursor.getString(columnLabel));

                phones.add(phone);
            }
        }
        return phones;
    }
    
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!activeQuery)
            return;

        List<ProviderPhone> phones = resolvePhonesFromCursor(cursor);

        for (SyncManagerListener listener : listeners) {
            listener.onPhoneQueryComplete(phones);
        }
        activeQuery = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // TODO Auto-generated method stub

    }

    private class SqlArgs{
         public String selection;
         public String[] params;
         public SqlArgs(String selection, String[] params){
             this.selection = selection;
             this.params = params;
         }
    }
}
