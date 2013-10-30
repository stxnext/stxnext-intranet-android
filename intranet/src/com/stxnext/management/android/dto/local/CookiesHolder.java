
package com.stxnext.management.android.dto.local;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class CookiesHolder extends AbstractMessage {

    @Expose
    @SerializedName("cookies")
    List<LocalCookie> cookies = new ArrayList<LocalCookie>();

    public List<LocalCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<LocalCookie> cookies) {
        this.cookies = cookies;
    }

}
