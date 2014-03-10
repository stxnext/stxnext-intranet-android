package com.stxnext.management.android;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.stxnext.management.android.games.poker.multiplayer.parse.ParseUtils;

public class AppIntranet extends Application{

    public static final String APP_ID = "RoWOuXZtb4ZayNWnVqQ01eaEgXYffaoa9gIJeBH0";
    public static final String CLIENT_ID = "lhBjPYQqp1hwnH1W02ZCRD1weSzfrrc6fxtCucqw";
    public static final String REST_API_KEY = "sJkLNGfkLkicjt5M5SifJu30tI0deamz98UQl5Rv";
    
    private static AppIntranet _instance;
    
    public static AppIntranet getApp(){
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        initParse();
    }
    
    private void initParse(){
        Parse.initialize(this, APP_ID, CLIENT_ID); 
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUtils.registerForChannelUpdates(this);
    }
}
