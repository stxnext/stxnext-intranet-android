
package com.stxnext.management.android.storage.sqlite;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.base.Throwables;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private DatabaseSchemaPopulator populator;

    public DatabaseOpenHelper(Application context, String name, int version,
            DatabaseSchemaPopulator populator) {
        super(context, name, null, version);
        this.populator = populator;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            populator.populate(db);
        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            for (int i = (oldVersion + 1); i <= newVersion; i++) {
                Log.e("", "upgrading database for version " + i);
                populator.update(db, i);
            }

            Log.e("", "upgrading database from"+oldVersion+" to "+newVersion);
            
            
        } catch (Exception e) {
            Throwables.propagate(e);
        }
    }
    

}
