package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.Lateness;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class LatenessMapper  implements EntityMapper<Lateness>, LatenessColumns {

    @Override
    public List<Lateness> mapEntity(Cursor c) {
        List<Lateness> result = new ArrayList<Lateness>();

        while (c.moveToNext()) {
            Lateness lateness = new Lateness();

            lateness.setLateId(c.getLong(c.getColumnIndex(LATE_ID)));
            lateness.setEnd(c.getString(c.getColumnIndex(END)));
            lateness.setId(c.getLong(c.getColumnIndex(USER_ID)));
            lateness.setName(c.getString(c.getColumnIndex(NAME)));
            lateness.setExplanation(c.getString(c.getColumnIndex(EXPLANATION)));
            lateness.setStart(c.getString(c.getColumnIndex(START)));
            lateness.setWorkFromHome(c.getInt(c.getColumnIndex(WORK_FROM_HOME))==1);

            result.add(lateness);
        }

        return result;
    }

}
