
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
    Number remarks;

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

    public Number getRemarks() {
        return remarks;
    }

}
