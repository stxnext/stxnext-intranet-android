package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.stxnext.management.android.dto.local.Absence;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class TeamDao extends AbstractDAO implements TeamColumns {

    private EntityMapper<Team> mapper;

    public TeamDao() {
        this.mapper = new TeamMapper();
    }

    public List<Team> fetch(){
        List<Team> result = new ArrayList<Team>();
        Cursor c = db.query(TABLE, null, null, null, null, null, NAME+" asc");
        result = mapper.mapEntity(c);
        c.close();
        
        for(Team team : result){
            Cursor userCursor = db.query(TeamUserColumns.TABLE, null, TeamUserColumns.TEAM_ID+"=?", new String[]{String.valueOf(team.getId().longValue())}, null, null, null);
            while(userCursor.moveToNext()){
                team.addUserId(userCursor.getLong(userCursor.getColumnIndex(TeamUserColumns.USER_ID)));
            }
            userCursor.close();
        }
        return result;
    }
    
    public void persist(List<Team> teams){
        db.beginTransaction();

        try {
            for (Team team : teams) {
                ContentValues cv = new ContentValues();
                cv.put(NAME, team.getName());
                cv.put(IMAGE, team.getImageUrl());
                //may crash
                if(team.getId() == null)
                    continue;
                    
                cv.put(EXTERNAL_ID, team.getId().longValue());
                insertOrReplace(TABLE, cv);
                
                for(Number userId : team.getUserIds()){
                    ContentValues userTeamCv = new ContentValues();
                    userTeamCv.put(TeamUserColumns.TEAM_ID, team.getId().longValue());
                    userTeamCv.put(TeamUserColumns.USER_ID, userId.longValue());
                    insertOrReplace(TeamUserColumns.TABLE, userTeamCv);
                }
                
                db.yieldIfContendedSafely();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("", "", e);
        } finally {
            db.endTransaction();
        }
    }
    
    public void clear(){
        db.delete(TABLE, null, null);
        db.delete(TeamUserColumns.TABLE, null, null);
    }

    @Override
    protected String getTableName() {
        return TABLE;
    }

}
