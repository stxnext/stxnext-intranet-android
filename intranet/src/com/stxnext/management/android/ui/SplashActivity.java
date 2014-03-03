
package com.stxnext.management.android.ui;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.ImageView;

import com.stxnext.management.android.R;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.ui.dependencies.AsyncTaskEx;
import com.stxnext.management.android.web.api.IntranetApi;

public class SplashActivity extends Activity {

    private static final long MIN_MILLIS_DISPLAY_TIME = 1200;

    ImageView splashImageView;
    long timeStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        splashImageView = (ImageView) findViewById(R.id.splashImageView);
        splashImageView.setImageResource(R.drawable.splash);
        
        timeStart = new Date().getTime();

        Display display = getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        int requestingOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        if (orientation == Surface.ROTATION_90) {
            requestingOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        }
        else if (orientation == Surface.ROTATION_180) {
            requestingOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
        }
        else if (orientation == Surface.ROTATION_270) {
            requestingOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        }
        setRequestedOrientation(requestingOrientation);
        launchingProcedure();
    }

    private void launchingProcedure() {
        new AsyncTaskEx<Void, Void, Void>() {

            long estimatedFinish = timeStart + MIN_MILLIS_DISPLAY_TIME;

            @Override
            protected void onPreExecute() {
                prepareAppInMain();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                prepareAppInBackground();
                while (new Date().getTime() < estimatedFinish) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Log.e("splash", "sleeping thread interrupted", e);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        // nope, just please wait as splash do its job
    }

    private void prepareAppInMain() {
        DAO.getInstance();
        IntranetApi.getInstance(getApplication());
    }

    private void prepareAppInBackground() {

    }

    @Override
    protected void onDestroy() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onDestroy();
    }
}
