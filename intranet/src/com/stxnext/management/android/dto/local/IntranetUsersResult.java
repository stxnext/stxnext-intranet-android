package com.stxnext.management.android.dto.local;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class IntranetUsersResult extends AbstractMessage{

    @Expose
    @SerializedName("users")
    private List<IntranetUser> users = new ArrayList<IntranetUser>();

    public List<IntranetUser> getUsers() {
        return users;
    }
}
