package com.stxnext.management.android.dto.local.postmessage;


public class LateMessage {

    String token;
    String hourStart;
    String minuteStart;
    String hourEnd;
    String minuteEnd;
    String lateStart;
    String lateEnd;
    
    public void setToken(String token) {
        this.token = token;
    }

    public void setHourStart(String hourStart) {
        this.hourStart = hourStart;
    }

    public void setMinuteStart(String minuteStart) {
        this.minuteStart = minuteStart;
    }

    public void setHourEnd(String hourEnd) {
        this.hourEnd = hourEnd;
    }

    public void setMinuteEnd(String minuteEnd) {
        this.minuteEnd = minuteEnd;
    }

    public void setLateStart(String lateStart) {
        this.lateStart = lateStart;
    }

    public void setLateEnd(String lateEnd) {
        this.lateEnd = lateEnd;
    }
    
    
    
}
