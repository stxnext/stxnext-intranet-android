package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class TeamMapper implements EntityMapper<Team>, TeamColumns {

    @Override
    public List<Team> mapEntity(Cursor c) {
        List<Team> result = new ArrayList<Team>();
        while (c.moveToNext()) {
            result.add(mapEntity(c, c.getPosition()));
        }
        return result;
    }

    @Override
    public Team mapEntity(Cursor c, int position) {
        Team team = new Team();
        team.setId(c.getLong(c.getColumnIndex(EXTERNAL_ID)));
        team.setImageUrl(c.getString(c.getColumnIndex(IMAGE)));
        team.setName(c.getString(c.getColumnIndex(NAME)));
        return team;
    }

}
