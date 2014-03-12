
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;

public class Project extends AbstractMessage {

    public interface ProjectFields{
        public static final String ENTITY_NAME = "project";
        public static final String THIS_MONTH_WORKED_HOURS = "this_month_worked_hours";
        public static final String EXTERNAL_ID = "id";
        public static final String CLIENT = "client";
        
        public static final String NAME = "name";
        public static final String LAST_MONTH_WORKED_HOURS = "last_month_worked_hours";
    }
    
    @Expose
    @SerializedName(ProjectFields.THIS_MONTH_WORKED_HOURS)
    Number thisMonthHours;

    @Expose
    @SerializedName(ProjectFields.CLIENT)
    Client client;

    @Expose
    @SerializedName(ProjectFields.EXTERNAL_ID)
    Number id;

    @Expose
    @SerializedName(ProjectFields.NAME)
    String name;

    @Expose
    @SerializedName(ProjectFields.LAST_MONTH_WORKED_HOURS)
    Number lastMonthHours;

    public Number getThisMonthHours() {
        return thisMonthHours;
    }

    public Client getClient() {
        return client;
    }

    public Number getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Number getLastMonthHours() {
        return lastMonthHours;
    }

}
