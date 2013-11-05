
package com.stxnext.management.android.dto.local;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class PresenceResult extends AbstractMessage {

    @Expose
    @SerializedName("blacklist")
    List<Number> blacklist = new ArrayList<Number>();

    @Expose
    @SerializedName("lates")
    List<Lateness> lates = new ArrayList<Lateness>();

    @Expose
    @SerializedName("absences")
    List<Absence> absences = new ArrayList<Absence>();

    public List<Number> getBlacklist() {
        return blacklist;
    }

    public List<Lateness> getLates() {
        return lates;
    }

    public List<Absence> getAbsences() {
        return absences;
    }
}
