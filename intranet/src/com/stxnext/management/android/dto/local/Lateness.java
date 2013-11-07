
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class Lateness extends AbstractMessage {

    @Expose
    @SerializedName("id")
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

    public void setId(Number id) {
        this.id = id;
    }

    public void setLateId(Number lateId) {
        this.lateId = lateId;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setWorkFromHome(Boolean workFromHome) {
        this.workFromHome = workFromHome;
    }

    
    
}
