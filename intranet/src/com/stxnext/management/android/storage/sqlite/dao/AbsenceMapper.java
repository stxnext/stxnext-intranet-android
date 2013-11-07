
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.Absence;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class AbsenceMapper implements EntityMapper<Absence>, AbsenceColumns {

    @Override
    public List<Absence> mapEntity(Cursor c) {
        List<Absence> result = new ArrayList<Absence>();

        while (c.moveToNext()) {
            Absence absence = new Absence();

            absence.setAbstenceId(c.getLong(c.getColumnIndex(ABSENCE_ID)));
            absence.setEnd(c.getString(c.getColumnIndex(END)));
            absence.setId(c.getLong(c.getColumnIndex(USER_ID)));
            absence.setName(c.getString(c.getColumnIndex(NAME)));
            absence.setRemarks(c.getString(c.getColumnIndex(REMARKS)));
            absence.setStart(c.getString(c.getColumnIndex(START)));

            result.add(absence);
        }

        return result;
    }

}
