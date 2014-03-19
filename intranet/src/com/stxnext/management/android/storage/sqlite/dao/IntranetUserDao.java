
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class IntranetUserDao extends AbstractDAO implements IntranetUserColumns {

    private EntityMapper<IntranetUser> mapper;

    public IntranetUserDao() {
        this.mapper = new IntranetUserMapper(true);
    }

    public List<IntranetUser> fetch() {
        List<IntranetUser> result = new ArrayList<IntranetUser>();
        Cursor c = db.query(TABLE, null, null, null, null, null, null);
        result = mapper.mapEntity(c);
        c.close();
        return result;
    }

    public List<IntranetUser> fetchFiltered() {
        List<IntranetUser> result = new ArrayList<IntranetUser>();
        Cursor c = fetchFilteredCursor(null);
        result = mapper.mapEntity(c);
        c.close();
        return result;
    }
    
    public IntranetUser getById(Long userId){
        Cursor cursor = db.rawQuery(BASE_USER_QUERY+" AND u."+EXTERNAL_ID+"=? limit 1", new String[]{String.valueOf(userId)});
        return Iterables.getFirst(mapper.mapEntity(cursor), null);
    }

    private static final String BASE_USER_QUERY = "select u."+EXTERNAL_ID+" as _id," +
    		"ab."+AbsenceColumns.END+" as "+JOIN_ABSENCE_END+", " +"ab."+AbsenceColumns.START+" as "+JOIN_ABSENCE_START+", " +"ab."+AbsenceColumns.REMARKS+" as "+JOIN_ABSENCE_REMARKS+", " +
    		"la."+LatenessColumns.END+" as "+JOIN_LATENESS_END+", " +"la."+LatenessColumns.START+" as "+JOIN_LATENESS_START+", " +"la."+LatenessColumns.EXPLANATION+" as "+JOIN_LATENESS_EXPLANATION +","+
    		" u.* from "+TABLE+" u" +
    		" left join "+AbsenceColumns.TABLE+" ab on ab."+AbsenceColumns.USER_ID+"=u."+EXTERNAL_ID+"" +
            " left join "+LatenessColumns.TABLE+" la on la."+LatenessColumns.USER_ID+"=u."+EXTERNAL_ID+
    				" where u."+IS_CLIENT+"=? AND u."+IS_ACTIVE+"=? ";
    
    public Cursor fetchFilteredCursor(String query) {
        String[] queryParams = new String[] {
                "0", "1"
        };
        String queryFilter = "";
        if(!Strings.isNullOrEmpty(query)){
            queryFilter = " AND u."+NAME+" like ? ";
            queryParams = new String[] {
                    "0", "1","%"+query+"%"
            };
        }
        Cursor cursor = db.rawQuery(BASE_USER_QUERY+queryFilter+" group by u."+EXTERNAL_ID+" order by u."+NAME+" asc", queryParams);

        return cursor;
    }
    
    
    public int getEntityCount(){
        Cursor cursor = db.rawQuery("select count (_id) from ("+BASE_USER_QUERY+")", new String[] {
                "0", "1"
        });
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        return result;
    }
    

    public void persist(List<IntranetUser> users) {
        db.beginTransaction();

        try {
            for (IntranetUser user : users) {
                ContentValues cv = new ContentValues();
                cv.put(EXTERNAL_ID, user.getId().longValue());

                cv.put(NAME, user.getName());
                cv.put(IMG, user.getImageUrl());
                cv.put(AVATAR_URL, user.getAvatarUrl());
                cv.put(LOCATION, user.getLocationJson());
                cv.put(IS_FREELANCER, user.getIsFreelancer());
                cv.put(IS_CLIENT, user.getIsClient());
                cv.put(IS_ACTIVE, user.getIsActive());
                // TODO : create start stop work formatters!
                // cv.put(START_WORK, user.getStartWork());
                // cv.put(START_FULLTIME_WORK, user.getStartWork());
                cv.put(PHONE, user.getPhone());
                cv.put(PHONE_ON_DESK, user.getPhoneDesk());
                cv.put(SKYPE, user.getSkype());
                cv.put(IRC, user.getIrc());
                cv.put(EMAIL, user.getEmail());
                cv.put(TASKS_LINK, user.getTasksLink());
                cv.put(AVAILABILITY_LINK, user.getAvailabilityLink());
                cv.put(ROLES, user.getRolesJson());
                cv.put(GROUPS, user.getGroupsJson());

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
