
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.AbsenceDisplayData;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class IntranetUserMapper implements EntityMapper<IntranetUser>, IntranetUserColumns {

    @Override
    public List<IntranetUser> mapEntity(Cursor c) {
        List<IntranetUser> result = new ArrayList<IntranetUser>();

        while (c.moveToNext()) {
            IntranetUser u = mapSingle(c);
            result.add(u);
        }

        return result;
    }

    @Override
    public IntranetUser mapEntity(Cursor c, int position) {
        IntranetUser u = null;
        if (c.moveToPosition(position)) {
            u = mapSingle(c);
        }
        return u;
    }
    
    private IntranetUser mapSingle(Cursor c){
        IntranetUser u = new IntranetUser();
        
        u.setAvailabilityLink(c.getString(c.getColumnIndex(AVAILABILITY_LINK)));
        u.setAvatarUrl(c.getString(c.getColumnIndex(AVATAR_URL)));
        u.setEmail(c.getString(c.getColumnIndex(EMAIL)));
        u.setGroups(c.getString(c.getColumnIndex(GROUPS)));
        u.setId(c.getLong(c.getColumnIndex(EXTERNAL_ID)));
        u.setImageUrl(c.getString(c.getColumnIndex(IMG)));
        u.setIrc(c.getString(c.getColumnIndex(IRC)));
        u.setIsActive(c.getInt(c.getColumnIndex(IS_ACTIVE)) == 1);
        u.setIsClient(c.getInt(c.getColumnIndex(IS_CLIENT)) == 1);
        u.setIsFreelancer(c.getInt(c.getColumnIndex(IS_FREELANCER)) == 1);
        u.setLocation(c.getString(c.getColumnIndex(LOCATION)));
        u.setName(c.getString(c.getColumnIndex(NAME)));
        u.setPhone(c.getString(c.getColumnIndex(PHONE)));
        u.setPhoneDesk(c.getString(c.getColumnIndex(PHONE_ON_DESK)));
        u.setRoles(c.getString(c.getColumnIndex(ROLES)));
        u.setSkype(c.getString(c.getColumnIndex(SKYPE)));
        //u.setStartFullTimeWork(c.getString(c.getColumnIndex(START_FULLTIME_WORK)));
        //u.setStopWork(c.getString(c.getColumnIndex(START_FULLTIME_WORK)));
        //u.setStartWork(startWork)
        u.setTasksLink(c.getString(c.getColumnIndex(TASKS_LINK)));
        
        
        //here goes the join... unfortunately we need to be able to fastload sqldata 
        //(cursor, quicksearch) and be able to display lateness/absence at the same time
        int absenceStartColumn = c.getColumnIndex(JOIN_ABSENCE_START);
        int absenceEndColumn = c.getColumnIndex(JOIN_ABSENCE_END);
        int absenceExplanationColumn = c.getColumnIndex(JOIN_ABSENCE_REMARKS);
        
        int latenessStartColumn = c.getColumnIndex(JOIN_LATENESS_START);
        int latenessEndColumn = c.getColumnIndex(JOIN_LATENESS_END);
        int latenessExplanationColumn = c.getColumnIndex(JOIN_LATENESS_EXPLANATION);
        
        if(!c.isNull(absenceStartColumn)||!c.isNull(absenceEndColumn)||!c.isNull(absenceExplanationColumn)){
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(absenceStartColumn);
            data.end = c.getString(absenceEndColumn);
            data.explanation = c.getString(absenceExplanationColumn);
            u.setAbsenceDisplayData(data);
        }
        
        if(!c.isNull(latenessStartColumn)||!c.isNull(latenessEndColumn)||!c.isNull(latenessExplanationColumn)){
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(latenessStartColumn);
            data.end = c.getString(latenessEndColumn);
            data.explanation = c.getString(latenessExplanationColumn);
            u.setLatenessDisplayData(data);
        }
        
        return u;
    }

}
