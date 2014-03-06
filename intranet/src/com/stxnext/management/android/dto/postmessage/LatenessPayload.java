package com.stxnext.management.android.dto.postmessage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatenessPayload extends AbstractMessage{

    @Expose
    @SerializedName("lateness")
    private LatenessMessage lateness;
    
    public LatenessPayload(LatenessMessage msg){
        this.lateness = msg;
    }

    public LatenessMessage getLateness() {
        return lateness;
    }
    
}
