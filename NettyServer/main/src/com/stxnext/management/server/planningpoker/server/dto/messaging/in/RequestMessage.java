package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

public class RequestMessage extends AbstractMessage{

    public static final String FIELD_REQUEST_NAME = "request_name";
    
    @Expose
    @SerializedName(FIELD_REQUEST_NAME)
    private String requestName;

    
    public String getRequestName() {
        return requestName;
    }


    @Override
    protected void prepareToSerialization() {
    }

}
