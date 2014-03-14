package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

public class IncomingMessage extends AbstractMessage{
    
    public static final String MESSAGE_REQUEST = "request";
    public static final String MESSAGE_SESSION = "game_session";
    
    @Expose
    @SerializedName(MESSAGE_REQUEST)
    private RequestMessage request;
    
    @Expose
    @SerializedName(MESSAGE_SESSION)
    private SessionMessage sessionMessage;

    public RequestMessage getRequest() {
        return request;
    }

    public SessionMessage getSessionMessage() {
        return sessionMessage;
    }

}
