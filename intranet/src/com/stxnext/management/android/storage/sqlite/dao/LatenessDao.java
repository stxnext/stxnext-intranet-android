
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.stxnext.management.android.dto.local.Lateness;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class LatenessDao extends AbstractDAO implements LatenessColumns {

    private EntityMapper<Lateness> mapper;

    public LatenessDao() {
        this.mapper = new LatenessMapper();
    }

    public List<Lateness> fetch(boolean workFromHome) {
        String selection = WORK_FROM_HOME+"=? ";
        String[] selectionArgs = new String[]{workFromHome?"1":"0"};
        List<Lateness> result = new ArrayList<Lateness>();
        Cursor c = db.query(TABLE, null, selection, selectionArgs, null, null, null);
        result = mapper.mapEntity(c);
        c.close();
        return result;
    }

    public void persist(List<Lateness> lates) {
        db.beginTransaction();

        try {
            for (Lateness late : lates) {
                ContentValues cv = new ContentValues();

                cv.put(LATE_ID, late.getLateId().longValue());
                cv.put(END, late.getEnd());
                cv.put(NAME, late.getName());
                cv.put(EXPLANATION, late.getExplanation());
                cv.put(START, late.getStart());
                cv.put(USER_ID, late.getId().longValue());
                cv.put(WORK_FROM_HOME, late.getWorkFromHome() != null ? (late.getWorkFromHome() ? 1
                        : 0) : 0);

                insertOrReplace(TABLE, cv);
                db.yieldIfContendedSafely();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("", "", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    protected String getTableName() {
        return TABLE;
    }
}
