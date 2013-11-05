package com.stxnext.management.android.storage.sqlite.dao;

import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.Absence;
import com.stxnext.management.android.storage.sqlite.EntityMapper;
import com.stxnext.management.android.storage.sqlite.dao.AbsenceDao.AbsenceColumns;

public class AbsenceMapper implements EntityMapper<Absence>, AbsenceColumns {

    @Override
    public List<Absence> mapEntity(Cursor c) {
        // TODO Auto-generated method stub
        return null;
    }


}
