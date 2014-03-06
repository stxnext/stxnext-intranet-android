package com.stxnext.management.android.dto.postmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AbsencePayload extends AbstractMessage{

    @Expose
    @SerializedName("absence")
    private AbsenceMessage absence;
    
    public AbsencePayload(AbsenceMessage msg){
        this.absence = msg;
    }

    public AbsenceMessage getAbsence() {
        return absence;
    }
    
}
