package com.stxnext.management.server.planningpoker.server.dto.messaging;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageWrapper extends AbstractMessage{

    
    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_RESPONSE = "response";
    public static final String TYPE_NOTIFICATION = "notification";
    
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_ACTION = "action";
    public static final String FIELD_PAYLOAD = "payload";
    
    @Expose
    @SerializedName(FIELD_TYPE)
    private String type;
    @Expose
    @SerializedName(FIELD_ACTION)
    private String action;
    @Expose
    @SerializedName(FIELD_PAYLOAD)
    private String payload;
    
    public MessageWrapper(String type, String action, String payload){
        this.type = type;
        this.action = action;
        this.payload = payload;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    
    
    
}
