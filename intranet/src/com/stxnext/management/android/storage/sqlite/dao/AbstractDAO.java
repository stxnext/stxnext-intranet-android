
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.stxnext.management.android.storage.sqlite.SQLiteHelperProvider;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDAO {

    protected SQLiteDatabase db;
    
    public AbstractDAO() {
        db = SQLiteHelperProvider.getInstance().get().getWritableDatabase();
    }

    protected abstract String getTableName();
    
    public void clear(){
        db.delete(getTableName(), null, null);
    }
    
    protected void insertOrReplace(String table, ContentValues cv) {

        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        List<Object> values = new ArrayList<Object>();

        for (Entry<String, Object> entry : cv.valueSet()) {
            if (keysBuilder.length() > 0) {
                keysBuilder.append(",");
            }
            keysBuilder.append(entry.getKey());

            if (valuesBuilder.length() > 0) {
                valuesBuilder.append(",");
            }
            valuesBuilder.append("?");
            values.add(entry.getValue());
        }

        StringBuilder sql = new StringBuilder("insert or replace into ").append(table)
                .append(" (");

        sql.append(keysBuilder);

        sql.append(") values (");

        sql.append(valuesBuilder);

        sql.append(");");

        db.execSQL(sql.toString(), values.toArray());
    }

}
