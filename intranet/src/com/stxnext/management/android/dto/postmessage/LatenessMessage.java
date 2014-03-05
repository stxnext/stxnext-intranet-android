package com.stxnext.management.android.dto.postmessage;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.ui.dependencies.TimeUtil;

public class LatenessMessage extends AbstractMessage{

    @Expose
    @SerializedName("late_start")
    String startHour;
    
    @Expose
    @SerializedName("late_end")
    String endHour;
    
    @Expose
    @SerializedName("popup_date")
    String submissionDate;
    
    @Expose
    @SerializedName("work_from_home")
    Boolean workFromHome;
    
    @Expose
    @SerializedName("popup_explanation")
    String explanation;

    public void setStartHour(Date startHour) {
        this.startHour = TimeUtil.defaultTimeFormat.format(startHour);
    }

    public void setEndHour(Date endHour) {
        this.endHour = TimeUtil.defaultTimeFormat.format(endHour);
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = TimeUtil.defaultDateFormat.format(submissionDate);
    }

    public void setWorkFromHome(Boolean workFromHome) {
        this.workFromHome = workFromHome;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    

    
    
}
