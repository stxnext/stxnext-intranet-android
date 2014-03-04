
package com.stxnext.management.android.dto.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MandatedTime {

    @Expose
    @SerializedName("mandated")
    Number mandated;

    @Expose
    @SerializedName("days")
    Number days;

    @Expose
    @SerializedName("left")
    Number left;

    public Number getMandated() {
        return mandated;
    }

    public Number getDays() {
        return days;
    }

    public Number getLeft() {
        return left;
    }

}
