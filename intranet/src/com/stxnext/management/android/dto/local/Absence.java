
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class Absence extends AbstractMessage {

    @Expose
    @SerializedName("id")
    Number id;
    @Expose
    @SerializedName("end")
    String end;
    @Expose
    @SerializedName("start")
    String start;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("absence_id")
    Number abstenceId;
    @Expose
    @SerializedName("remarks")
    String remarks;

    public Number getId() {
        return id;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public String getName() {
        return name;
    }

    public Number getAbstenceId() {
        return abstenceId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setId(Number id) {
        this.id = id;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbstenceId(Number abstenceId) {
        this.abstenceId = abstenceId;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    
    
    

}
