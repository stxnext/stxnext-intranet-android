package com.stxnext.management.android.dto.postmessage;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatenessMessage extends AbstractMessage{

    @Expose
    @SerializedName("late_start")
    String startDate;
    
    @Expose
    @SerializedName("late_end")
    String endDate;
    
    @Expose
    @SerializedName("popup_date")
    String submissionDate;
    
    @Expose
    @SerializedName("work_from_home")
    Boolean workFromHome;
    
    @Expose
    @SerializedName("popup_explanation")
    String explanation;

    public void setStartDate(Date startDate) {
        this.startDate = defaultDateFormat.format(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = defaultDateFormat.format(endDate);
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = defaultDateFormat.format(submissionDate);
    }

    public void setWorkFromHome(Boolean workFromHome) {
        this.workFromHome = workFromHome;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    

    
    
}
