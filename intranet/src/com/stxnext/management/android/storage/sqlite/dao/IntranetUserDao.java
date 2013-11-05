
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;


public class IntranetUserDao extends AbstractDAO implements IntranetUserColumns{

    private EntityMapper<IntranetUser> mapper;

    public IntranetUserDao() {
        this.mapper = new IntranetUserMapper();
    }

    public List<IntranetUser> fetch(){
        List<IntranetUser> result = new ArrayList<IntranetUser>();
        Cursor c = db.query(TABLE, null, null, null, null, null, null);
        result = mapper.mapEntity(c);
        c.close();
        return result;
    }
    
    public void persist(List<IntranetUser> users){
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
                //cv.put(START_WORK, user.getStartWork());
                //cv.put(START_FULLTIME_WORK, user.getStartWork());
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
    
}
