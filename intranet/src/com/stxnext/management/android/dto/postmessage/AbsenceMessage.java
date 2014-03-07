package com.stxnext.management.android.dto.postmessage;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.R;
import com.stxnext.management.android.ui.dependencies.TimeUtil;

public class AbsenceMessage extends AbstractMessage{

    public enum AbsenceType{
        PLANNED("planowany", R.string.absence_planned),
        LEAVE_AT_REQUEST("zadanie",R.string.absence_leave_at_request),
        ILLNESS("l4",R.string.absence_illness),
        COMPASSIONATE("okolicznosciowy",R.string.absence_compassionate),
        OTHER("inne",R.string.absence_compassionate);
        
        private String absenceName;
        private int resourceId;
        AbsenceType(String absenceName, int resourceId){
            this.absenceName = absenceName;
            this.resourceId = resourceId;
        }
        
        public String getAbsenceName(){
            return this.absenceName;
        }

        public int getResourceId() {
            return resourceId;
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
        this.startDate = TimeUtil.defaultDateFormat.format(startDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = TimeUtil.defaultDateFormat.format(endDate);
    }

    public void setAbsenceType(AbsenceType type) {
        this.absenceType = type.getAbsenceName();
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    
    
}
