
package com.stxnext.management.android.ui;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.stxnext.management.android.R;
import com.stxnext.management.android.dto.local.AbsenceDisplayData;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.UserProperty;
import com.stxnext.management.android.sync.ContactSyncManager;
import com.stxnext.management.android.sync.ContactSyncManager.ProviderPhone;
import com.stxnext.management.android.sync.ContactSyncManager.SyncManagerListener;
import com.stxnext.management.android.ui.controls.RoundedImageView;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.ui.dependencies.PropertyListAdapter;

public class UserDetailsActivity extends AbstractSimpleActivity implements SyncManagerListener{

    public static final String EXTRA_USER = "user";

    RoundedImageView userImageView;
    TextView nameView;
    ListView listView;
    TextView lateTimeView;
    TextView lateDescriptionView;
    PropertyListAdapter adapter;

    ViewGroup loadingView;
    ViewGroup loadedView;

    IntranetUser user;
    ContactSyncManager syncManager;

    @Override
    protected void fillViews() {
        userImageView = (RoundedImageView) findViewById(R.id.userImageView);
        nameView = (TextView) findViewById(R.id.nameView);
        listView = (ListView) findViewById(R.id.listView);
        loadingView = (ViewGroup) findViewById(R.id.loadingView);
        loadedView = (ViewGroup) findViewById(R.id.loadedView);
        lateTimeView = (TextView) findViewById(R.id.lateTimeView);
        lateDescriptionView = (TextView) findViewById(R.id.lateDescriptionView);

        // userImageView.setCornersRadius(12F);

        Bundle bundle = getIntent().getExtras();
        user = (IntranetUser) bundle.getSerializable(EXTRA_USER);

        syncManager = new ContactSyncManager(this);
        
        nameView.setText(user.getName());
        if (user.getAbsenceDisplayData() != null)
            insertAbsenceData(user.getAbsenceDisplayData(), lateTimeView,
                    lateDescriptionView);

        if (user.getLatenessDisplayData() != null)
            insertAbsenceData(user.getLatenessDisplayData(), lateTimeView,
                    lateDescriptionView);
        new LoadDataTask().execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_contact){
            addContactAction();
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void addContactAction(){
        syncManager.launchQuery(getLoaderManager(), user.getPhone(), user.getName());
    }

    private void insertAbsenceData(AbsenceDisplayData data,
            TextView lateTimeView, TextView lateDescriptionView) {
        if (data != null) {
            lateTimeView.setText(formatLatenessTime(data));
            lateDescriptionView
                    .setText(data.explanation);
            lateTimeView.setVisibility(View.VISIBLE);
            lateDescriptionView.setVisibility(View.VISIBLE);
        } else {
            lateTimeView.setVisibility(View.GONE);
            lateDescriptionView.setVisibility(View.GONE);
        }
    }

    private String formatLatenessTime(AbsenceDisplayData data) {
        if (data.start != null && data.end != null) {
            return data.start + " - " + data.end;
        }
        return null;
    }

    @Override
    protected void setActions() {

    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_user_details;
    }

    private void setViewLoading(boolean loading) {
        loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
        loadedView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private class LoadDataTask extends
            AsyncTaskEx<Void, Void, List<UserProperty>> {

        Bitmap bmp;

        @Override
        protected void onPreExecute() {
            setViewLoading(true);
            super.onPreExecute();
        }

        @Override
        protected List<UserProperty> doInBackground(Void... params) {
            bmp = BitmapUtils.getTempBitmap(UserDetailsActivity.this, user
                    .getId().toString());
            return user.getProperties();
        }

        @Override
        protected void onPostExecute(List<UserProperty> result) {
            super.onPostExecute(result);
            if (isFinishing())
                return;

            if (bmp != null) {
                userImageView.setImageBitmap(bmp);
            }
            adapter = new PropertyListAdapter(UserDetailsActivity.this,
                    listView, result);
            listView.setAdapter(adapter);
            setViewLoading(false);
            applyListAnimation(listView);
        }
    }

    private void prepareAndMergePhones(List<ProviderPhone> phones){
        for(ProviderPhone phone : phones){
            phone.setNumberToUpdate(user.getPhone());
        }
        syncManager.mergeContacts(phones, user);
    }
    
    // Content provider related
    private void showImportDialog(String content, final List<ProviderPhone> phones){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                this);
        alertDialog.setTitle("Import");
        alertDialog.setMessage(content);
         
        alertDialog.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAndMergePhones(phones);
                        Toast.makeText(getApplicationContext(),
                                "You clicked on YES", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
        alertDialog.setNegativeButton("Nie",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });
         
        alertDialog.show();
    }
    
    @Override
    public void onPhoneQueryComplete(List<ProviderPhone> phones) {
        if(isFinishing())
            return;
        
        StringBuilder content = new StringBuilder();
        if(phones.size()>0){
            content.append("Znaleziono następujące kontakty pasujące do osoby:\n");
            for(ProviderPhone phone : phones){
                content.append(phone.getDisplayName()).append("\n").append(phone.getPhoneNumber());
                content.append("\n\n");
            }
        }
        else{
            ProviderPhone phone = syncManager.new ProviderPhone();
            phone.setDisplayName(user.getName());
            phone.setNumberToUpdate(user.getPhone());
            phones.add(phone);
        }
        content.append("Czy dodać nowy numer jako numer STXNext i oraz dodać kontakt do grupy STX?\n(Kontakt pozostanie w dotychczasowych grupach)");
        Log.e("","cursor loading finished");
        showImportDialog(content.toString(),phones);
    }
}
