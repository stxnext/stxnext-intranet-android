package com.stxnext.management.android.storage.sqlite.dao;

import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.dto.local.Lateness;
import com.stxnext.management.android.storage.sqlite.EntityMapper;
import com.stxnext.management.android.storage.sqlite.dao.AbsenceDao.AbsenceColumns;
import com.stxnext.management.android.storage.sqlite.dao.LatenessDao.LatenessColumns;

public class LatenessMapper  implements EntityMapper<Lateness>, LatenessColumns {

    @Override
    public List<Lateness> mapEntity(Cursor c) {
        // TODO Auto-generated method stub
        return null;
    }

}
