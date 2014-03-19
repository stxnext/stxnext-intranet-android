
package com.stxnext.management.server.planningpoker.server.dto.messaging.in;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.server.planningpoker.server.dto.combined.Player;
import com.stxnext.management.server.planningpoker.server.dto.combined.Session;
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

    public SessionMessage(Player player, Session session, String serializedSubject){
        this.playerId = player.getId();
        this.sessionId = session.getId();
        this.sessionSubject = serializedSubject;
    }
    
    public SessionMessage(Long playerId, Long sessionId, String serializedSubject){
        this.playerId = playerId;
        this.sessionId = sessionId;
        this.sessionSubject = serializedSubject;
    }
    
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

    @Override
    public void prepareToSerialization() {
    }

}
