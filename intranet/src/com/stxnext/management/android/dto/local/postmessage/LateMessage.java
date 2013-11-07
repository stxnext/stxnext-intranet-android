package com.stxnext.management.android.dto.local.postmessage;

import java.util.LinkedList;
import java.util.List;

import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;

public class LateMessage {

    String token;
    String hourStart;
    String minuteStart;
    String hourEnd;
    String minuteEnd;
    String lateStart;
    String lateEnd;
    
    public List<NameValuePair> toPostParams(){
        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("csrf_token", token));
        
        params.add(new BasicNameValuePair("hour", token));
        params.add(new BasicNameValuePair("minute", token));
        params.add(new BasicNameValuePair("late_start", token));
        
        params.add(new BasicNameValuePair("hour", token));
        params.add(new BasicNameValuePair("minute", token));
        params.add(new BasicNameValuePair("late_end", token));

        return toPostParams();
    }

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
