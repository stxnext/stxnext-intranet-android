
package com.stxnext.management.android.sync;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

import com.google.common.base.Strings;
import com.stxnext.management.android.dto.local.IntranetUser;

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

    private ContactSyncManager() {
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
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    phone.getNumberToUpdate())
                            .withValue(Phone.LABEL, CONTACT_LABEL + " Mobile")
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
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
                else {
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
                    context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                }

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

    public void launchQuery(LoaderManager loader, String phoneTerm, String nameTerm) {
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

    public Cursor mCursor;
    public int mLookupKeyIndex;
    public int mIdIndex;
    public String mCurrentLookupKey;
    public long mCurrentId;
    Uri mSelectedContactUri;

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        List<String> selectionArgs = new ArrayList<String>();

        String SELECTION = "";
        if (!Strings.isNullOrEmpty(mNameSearchQuery)) {
            SELECTION += PhoneNameColumn + " LIKE ?";
            selectionArgs.add("%" + mNameSearchQuery + "%");
        }

        if (!Strings.isNullOrEmpty(mPhoneSearchQuery)) {
            if (selectionArgs.size() > 0) {
                SELECTION = SELECTION + " OR ";
            }

            SELECTION += "(" + Phone.NUMBER + " LIKE ? AND " + Phone.LABEL + " LIKE ?)";
            selectionArgs.add("%" + mPhoneSearchQuery + "%");
            selectionArgs.add("%" + CONTACT_LABEL + "%");
        }

        String[] array = new String[selectionArgs.size()];
        selectionArgs.toArray(array);

        // Starts the query
        return new CursorLoader(
                context,
                Phone.CONTENT_URI,
                PROJECTION,
                SELECTION,
                array,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (!activeQuery)
            return;

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
                    phone.displayName = cursor.getString(columnName);
                if (!cursor.isNull(columnNumber))
                    phone.phoneNumber = cursor.getString(columnNumber);
                if (!cursor.isNull(columnId))
                    phone.id = cursor.getLong(columnId);
                if (!cursor.isNull(columnContactId))
                    phone.contactId = cursor.getLong(columnContactId);
                if (!cursor.isNull(columnType))
                    phone.type = cursor.getString(columnContactId);
                if (!cursor.isNull(columnLabel))
                    phone.label = cursor.getString(columnLabel);

                phones.add(phone);
            }
        }

        for (SyncManagerListener listener : listeners) {
            listener.onPhoneQueryComplete(phones);
        }
        activeQuery = false;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // TODO Auto-generated method stub

    }

    public class ProviderPhone {
        private String displayName;
        private String phoneNumber;
        private Long id;
        private Long contactId;
        private String type;
        private String label;

        private String numberToUpdate;

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setContactId(Long contactId) {
            this.contactId = contactId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Long getId() {
            return id;
        }

        public Long getContactId() {
            return contactId;
        }

        public String getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public String getNumberToUpdate() {
            return numberToUpdate;
        }

        public void setNumberToUpdate(String numberToUpdate) {
            this.numberToUpdate = numberToUpdate;
        }

    }
}
