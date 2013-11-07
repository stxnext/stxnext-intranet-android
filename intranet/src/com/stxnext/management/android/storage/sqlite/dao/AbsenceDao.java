package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.stxnext.management.android.dto.local.Absence;
import com.stxnext.management.android.storage.sqlite.EntityMapper;


public class AbsenceDao extends AbstractDAO implements AbsenceColumns{

    private EntityMapper<Absence> mapper;

    public AbsenceDao() {
        this.mapper = new AbsenceMapper();
    }

    public List<Absence> fetch(){
        List<Absence> result = new ArrayList<Absence>();
        Cursor c = db.query(TABLE, null, null, null, null, null, null);
        result = mapper.mapEntity(c);
        c.close();
        return result;
    }
    
    public void persist(List<Absence> absences){
        db.beginTransaction();

        try {
            for (Absence absence : absences) {
                ContentValues cv = new ContentValues();
                
                cv.put(ABSENCE_ID, absence.getAbstenceId().longValue());
                cv.put(END, absence.getEnd());
                cv.put(NAME, absence.getName());
                cv.put(REMARKS, absence.getRemarks());
                cv.put(START, absence.getStart());
                cv.put(USER_ID, absence.getId().longValue());
                
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
