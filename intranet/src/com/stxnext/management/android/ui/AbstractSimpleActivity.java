package com.stxnext.management.android.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.stxnext.management.android.dependencies.googleplay.GooglePlayServiceErrorMessages;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.api.IntranetApi;

public abstract class AbstractSimpleActivity extends Activity{
    //common tools
    protected StoragePrefs prefs;
    protected IntranetApi api;
    
    //required methods
    protected abstract void fillViews();
    protected abstract void setActions();
    protected abstract int getContentResourceId();
    //the rest
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyWindowSettings();
        prefs = StoragePrefs.getInstance(this);
        api = IntranetApi.getInstance(getApplication());
        setContentView(getContentResourceId());
        fillViews();
        setActions();
    }
    
    protected void applyWindowSettings(){
        
    }
    
    protected boolean isUserSignedIn(){
        return api.isUserSignedIn();
    }
    
    protected boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            showErrorDialog(resultCode);
            return false;
        }
    }
    
    
    private void showErrorDialog(int errorCode) {

        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
            errorCode,
            this,
            GooglePlayServiceErrorMessages.CONNECTION_FAILURE_RESOLUTION_REQUEST);

        if (errorDialog != null) {
            errorDialog.setOnCancelListener(cancelListener);
            errorDialog.show();
        }
        else{
            Builder builder = new android.app.AlertDialog.Builder(this)
            .setTitle("Update Google Play")
            .setMessage(GooglePlayServiceErrorMessages.getErrorString(this, errorCode))
            .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) { 
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_PACKAGE)));
                    finish();
                }
             });
            builder.setOnCancelListener(cancelListener);
            builder.show();
             //.show();
        }
    }
    
    private OnCancelListener cancelListener = new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            finish();
        }
    };
    
    protected void applyListAnimation(ViewGroup view) {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(350);
        set.addAnimation(animation);
        animation = new AlphaAnimation(0.1f, 1.1f);
        animation.setDuration(80);
        animation.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.1f);
        view.setLayoutAnimation(controller);
    }
    
}
