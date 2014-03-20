
package com.stxnext.management.android.storage.sqlite;

import android.database.sqlite.SQLiteOpenHelper;

import com.stxnext.management.android.AppIntranet;

public class SQLiteHelperProvider {

    private static SQLiteHelperProvider _instance;
    
    public static SQLiteHelperProvider getInstance(){
        if(_instance == null){
            _instance = new SQLiteHelperProvider();
        }
        return _instance;
    }
    
    private SQLiteHelperProvider(){
        this.populator = new DatabaseSchemaPopulator(AppIntranet.getApp());
    }
    
    private final static String DATABASE_NAME = "Intranet";
    private final static int DATABASE_VERSION = 2;

    DatabaseSchemaPopulator populator;
    SQLiteOpenHelper helper;

    public SQLiteOpenHelper get() {
        if(helper == null){
            helper =  new DatabaseOpenHelper(AppIntranet.getApp(), DATABASE_NAME, DATABASE_VERSION, populator);
        }
        return helper;
    }

}
