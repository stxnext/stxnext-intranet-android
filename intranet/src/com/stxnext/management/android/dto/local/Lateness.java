
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class Lateness extends AbstractMessage {

    @Expose
    @SerializedName("avatar_url")
    Number id;
    @Expose
    @SerializedName("late_id")
    Number lateId;
    @Expose
    @SerializedName("start")
    String start;
    @Expose
    @SerializedName("end")
    String end;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("explanation")
    String explanation;
    @Expose
    @SerializedName("work_from_home")
    Boolean workFromHome;

    public Number getId() {
        return id;
    }

    public Number getLateId() {
        return lateId;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getExplanation() {
        return explanation;
    }

    public Boolean getWorkFromHome() {
        return workFromHome;
    }

}
