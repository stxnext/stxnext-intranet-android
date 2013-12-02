
package com.stxnext.management.android.storage.sqlite.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.stxnext.management.android.dto.local.AbsenceDisplayData;
import com.stxnext.management.android.dto.local.IntranetUser;
import com.stxnext.management.android.storage.sqlite.EntityMapper;

public class IntranetUserMapper implements EntityMapper<IntranetUser>, IntranetUserColumns {

    boolean cacheColumnPosition;
    private static final int COLUMNS_COUNT = 23;

    int[] cachedPositions;

    public IntranetUserMapper() {
    }

    public IntranetUserMapper(boolean cacheColumnPosition) {
        this.cacheColumnPosition = cacheColumnPosition;
        this.cachedPositions = null;
    }

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

    private void prepareColumns(Cursor c) {
        if (cacheColumnPosition) {
            if (cachedPositions == null) {
                cachedPositions = new int[COLUMNS_COUNT];
                cachedPositions[0] = c.getColumnIndex(AVAILABILITY_LINK);
                cachedPositions[1] = c.getColumnIndex(AVATAR_URL);
                cachedPositions[2] = c.getColumnIndex(EMAIL);
                cachedPositions[3] = c.getColumnIndex(GROUPS);
                cachedPositions[4] = c.getColumnIndex(EXTERNAL_ID);
                cachedPositions[5] = c.getColumnIndex(IMG);
                cachedPositions[6] = c.getColumnIndex(IRC);
                cachedPositions[7] = c.getColumnIndex(IS_ACTIVE);
                cachedPositions[8] = c.getColumnIndex(IS_CLIENT);
                cachedPositions[9] = c.getColumnIndex(IS_FREELANCER);
                cachedPositions[10] = c.getColumnIndex(LOCATION);
                cachedPositions[11] = c.getColumnIndex(NAME);
                cachedPositions[12] = c.getColumnIndex(PHONE);
                cachedPositions[13] = c.getColumnIndex(PHONE_ON_DESK);
                cachedPositions[14] = c.getColumnIndex(ROLES);
                cachedPositions[15] = c.getColumnIndex(SKYPE);
                cachedPositions[16] = c.getColumnIndex(TASKS_LINK);
                cachedPositions[17] = c.getColumnIndex(JOIN_ABSENCE_START);
                cachedPositions[18] = c.getColumnIndex(JOIN_ABSENCE_END);
                cachedPositions[19] = c.getColumnIndex(JOIN_ABSENCE_REMARKS);
                cachedPositions[20] = c.getColumnIndex(JOIN_LATENESS_START);
                cachedPositions[21] = c.getColumnIndex(JOIN_LATENESS_END);
                cachedPositions[22] = c.getColumnIndex(JOIN_LATENESS_EXPLANATION);
            }
        }
    }

    private IntranetUser mapSingle(Cursor c) {
        prepareColumns(c);
        if (cacheColumnPosition) {
            return inflateFromPositions(c);
        }
        else {
            return inflateSimple(c);
        }
    }

    private IntranetUser inflateFromPositions(Cursor c) {
        IntranetUser u = new IntranetUser();
        u.setAvailabilityLink(c.getString(cachedPositions[0]));
        u.setAvatarUrl(c.getString(cachedPositions[1]));
        u.setEmail(c.getString(cachedPositions[2]));
        u.setGroups(c.getString(cachedPositions[3]));
        u.setId(c.getLong(cachedPositions[4]));
        u.setImageUrl(c.getString(cachedPositions[5]));
        u.setIrc(c.getString(cachedPositions[6]));
        u.setIsActive(c.getInt(cachedPositions[7]) == 1);
        u.setIsClient(c.getInt(cachedPositions[8]) == 1);
        u.setIsFreelancer(c.getInt(cachedPositions[9]) == 1);
        u.setLocation(c.getString(cachedPositions[10]));
        u.setName(c.getString(cachedPositions[11]));
        u.setPhone(c.getString(cachedPositions[12]));
        u.setPhoneDesk(c.getString(cachedPositions[13]));
        u.setRoles(c.getString(cachedPositions[14]));
        u.setSkype(c.getString(cachedPositions[15]));
        u.setTasksLink(c.getString(cachedPositions[16]));

        int absenceStartColumn = cachedPositions[17];
        int absenceEndColumn = cachedPositions[18];
        int absenceExplanationColumn = cachedPositions[19];

        int latenessStartColumn = cachedPositions[20];
        int latenessEndColumn = cachedPositions[21];
        int latenessExplanationColumn = cachedPositions[22];

        if (!c.isNull(absenceStartColumn) || !c.isNull(absenceEndColumn)
                || !c.isNull(absenceExplanationColumn)) {
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(absenceStartColumn);
            data.end = c.getString(absenceEndColumn);
            data.explanation = c.getString(absenceExplanationColumn);
            u.setAbsenceDisplayData(data);
        }

        if (!c.isNull(latenessStartColumn) || !c.isNull(latenessEndColumn)
                || !c.isNull(latenessExplanationColumn)) {
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(latenessStartColumn);
            data.end = c.getString(latenessEndColumn);
            data.explanation = c.getString(latenessExplanationColumn);
            u.setLatenessDisplayData(data);
        }

        return u;
    }

    private IntranetUser inflateSimple(Cursor c) {
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
        u.setTasksLink(c.getString(c.getColumnIndex(TASKS_LINK)));

        int absenceStartColumn = c.getColumnIndex(JOIN_ABSENCE_START);
        int absenceEndColumn = c.getColumnIndex(JOIN_ABSENCE_END);
        int absenceExplanationColumn = c.getColumnIndex(JOIN_ABSENCE_REMARKS);

        int latenessStartColumn = c.getColumnIndex(JOIN_LATENESS_START);
        int latenessEndColumn = c.getColumnIndex(JOIN_LATENESS_END);
        int latenessExplanationColumn = c.getColumnIndex(JOIN_LATENESS_EXPLANATION);

        if (!c.isNull(absenceStartColumn) || !c.isNull(absenceEndColumn)
                || !c.isNull(absenceExplanationColumn)) {
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(absenceStartColumn);
            data.end = c.getString(absenceEndColumn);
            data.explanation = c.getString(absenceExplanationColumn);
            u.setAbsenceDisplayData(data);
        }

        if (!c.isNull(latenessStartColumn) || !c.isNull(latenessEndColumn)
                || !c.isNull(latenessExplanationColumn)) {
            AbsenceDisplayData data = new AbsenceDisplayData();
            data.start = c.getString(latenessStartColumn);
            data.end = c.getString(latenessEndColumn);
            data.explanation = c.getString(latenessExplanationColumn);
            u.setLatenessDisplayData(data);
        }

        return u;
    }

}
