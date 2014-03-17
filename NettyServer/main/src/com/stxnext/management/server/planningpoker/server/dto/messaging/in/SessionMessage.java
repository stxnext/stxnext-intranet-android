
package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.server.planningpoker.server.dto.messaging.AbstractMessage;

public class SessionMessage extends AbstractMessage {

    @Expose
    @SerializedName("session_id")
    private Long sessionId;
    @Expose
    @SerializedName("player_id")
    private Long playerId;
    @Expose
    @SerializedName("session_subject")
    private String sessionSubject;

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getSessionSubject() {
        return sessionSubject;
    }

    public void setSessionSubject(String sessionSubject) {
        this.sessionSubject = sessionSubject;
    }

}
