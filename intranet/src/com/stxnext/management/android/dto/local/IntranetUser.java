package com.stxnext.management.android.dto.local;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;

public class IntranetUser extends AbstractMessage{

    @Expose
    @SerializedName("id")
    Number id;
    @Expose
    @SerializedName("name")
    String name;
    @Expose
    @SerializedName("img")
    String imageUrl;
    @Expose
    @SerializedName("avatar_url")
    String avatarUrl;
    @Expose
    @SerializedName("location")
    List<String> location;
    @Expose
    @SerializedName("freelancer")
    Boolean isFreelancer;
    @Expose
    @SerializedName("is_client")
    Boolean isClient;
    @Expose
    @SerializedName("is_active")
    Boolean isActive;
    @Expose
    @SerializedName("start_work")
    String startWork;
    @Expose
    @SerializedName("start_full_time_work")
    String startFullTimeWork;
    @Expose
    @SerializedName("stop_work")
    String stopWork;
    @Expose
    @SerializedName("phone")
    String phone;
    @Expose
    @SerializedName("phone_on_desk")
    String phoneDesk;
    @Expose
    @SerializedName("skype")
    String skype;
    @Expose
    @SerializedName("irc")
    String irc;
    @Expose
    @SerializedName("email")
    String email;
    @Expose
    @SerializedName("tasks_link")
    String tasksLink;
    @Expose
    @SerializedName("availability_link")
    String availabilityLink;
    @Expose
    @SerializedName("roles")
    List<String> roles;
    @Expose
    @SerializedName("groups")
    List<String> groups;
    
    
    public Number getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }
    public List<String> getLocation() {
        return location;
    }
    public Boolean getIsFreelancer() {
        return isFreelancer;
    }
    public Boolean getIsClient() {
        return isClient;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public String getStartWork() {
        return startWork;
    }
    public String getStartFullTimeWork() {
        return startFullTimeWork;
    }
    public String getStopWork() {
        return stopWork;
    }
    public String getPhone() {
        return phone;
    }
    public String getPhoneDesk() {
        return phoneDesk;
    }
    public String getSkype() {
        return skype;
    }
    public String getIrc() {
        return irc;
    }
    public String getEmail() {
        return email;
    }
    public String getTasksLink() {
        return tasksLink;
    }
    public String getAvailabilityLink() {
        return availabilityLink;
    }
    public List<String> getRoles() {
        return roles;
    }
    public List<String> getGroups() {
        return groups;
    }

    
}
