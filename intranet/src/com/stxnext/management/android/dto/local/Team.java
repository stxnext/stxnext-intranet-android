
package com.stxnext.management.android.dto.local;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;

public class Team extends AbstractMessage{

    public interface TeamFields{
        public static final String ENTITY_NAME = "team";
        
        public static final String USERS = "users";
        public static final String PROJECTS = "projects";
        public static final String EXTERNAL_ID = "id";
        public static final String IMG = "img";
        public static final String NAME = "name";
    }
    
    @Expose
    @SerializedName(TeamFields.USERS)
    List<Number> userIds = new ArrayList<Number>();

    @Expose
    @SerializedName(TeamFields.PROJECTS)
    List<Project> projects = new ArrayList<Project>();

    @Expose
    @SerializedName(TeamFields.EXTERNAL_ID)
    Number id;

    @Expose
    @SerializedName(TeamFields.IMG)
    String imageUrl;

    @Expose
    @SerializedName(TeamFields.NAME)
    String name;

    public List<Number> getUserIds() {
        return userIds;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public Number getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

}
