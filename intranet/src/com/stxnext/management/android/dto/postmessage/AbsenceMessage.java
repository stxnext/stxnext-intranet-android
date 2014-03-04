package com.stxnext.management.android.dto.postmessage;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AbsenceMessage extends AbstractMessage{

    public enum AbsenceType{
        PLANNED("planowany"),
        LEAVE_AT_REQUEST("zadanie"),
        ILLNESS("l4"),
        COMPASSIONATE("okolicznosciowy"),
        OTHER("inne");
        
        private String absenceName;
        AbsenceType(String absenceName){
            this.absenceName = absenceName;
        }
        
        public String getAbsenceName(){
            return this.absenceName;
        }
    }
    
    @Expose
    @SerializedName("popup_date_start")
    String startDate;
    
    @Expose
    @SerializedName("popup_date_end")
    String endDate;
    
    @Expose
    @SerializedName("popup_type")
    String absenceType;
    
    @Expose
    @SerializedName("popup_remarks")
    String remarks;

    
    
    public void setStartDate(Date startDate) {
        this.startDate = defaultDateFormat.format(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = defaultDateFormat.format(endDate);
    }

    public void setAbsenceType(AbsenceType type) {
        this.absenceType = type.getAbsenceName();
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    
    
}
