package com.stxnext.management.android.dto.local;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stxnext.management.android.dto.AbstractMessage;
import com.stxnext.management.android.dto.GsonProvider;

public class IntranetUser extends AbstractMessage implements Serializable{

    private ArrayList<UserProperty> properties = new ArrayList<UserProperty>();
    
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
    ArrayList<String> location = new ArrayList<String>();
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
    ArrayList<String> roles = new ArrayList<String>();
    @Expose
    @SerializedName("groups")
    ArrayList<String> groups = new ArrayList<String>();
    
    
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
    public String getLocationJson(){
        return GsonProvider.get().toJson(this.location);
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
    public String getRolesJson(){
        return GsonProvider.get().toJson(this.roles);
    }
    public List<String> getRoles() {
        return roles;
    }
    public String getGroupsJson(){
        return GsonProvider.get().toJson(this.groups);
    }
    public List<String> getGroups() {
        return groups;
    }

    public ArrayList<UserProperty> getProperties(){
        if(this.properties.size()<=0){
            putProperty("Email", "<a href = \"mailto:"+this.email+"\">"+this.email+"</a>");
            putProperty("IRC", this.irc);
            if(this.location!=null && this.location.size()==3)
                putProperty("Lokalizacja", this.location.get(1));
            putProperty("Telefon", "<a href = \"tel:"+this.phone+"\">"+this.phone+"</a>");
            putProperty("Telefon stacjonarny", "<a href = \"tel:"+this.phoneDesk+"\">"+this.phoneDesk+"</a>");
            if(this.roles!=null && this.roles.size()>0){
                String rolesString="";
                for(String role : this.roles){
                    rolesString+=role+"\n";
                }
                putProperty("Role", rolesString);
            }
            
            putProperty("Skype", this.skype);
        }
        return this.properties;
    }
    
    
    private void putProperty(String name, Object value){
        if(value!=null && !Strings.isNullOrEmpty(String.valueOf(value))){
            this.properties.add(new UserProperty(name, String.valueOf(value)));
        }
    }
    public void setProperties(ArrayList<UserProperty> properties) {
        this.properties = properties;
    }
    public void setId(Number id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public void setLocation(String location) {
        this.location = GsonProvider.get().fromJson(location, listStringType);
    }
    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }
    public void setIsFreelancer(Boolean isFreelancer) {
        this.isFreelancer = isFreelancer;
    }
    public void setIsClient(Boolean isClient) {
        this.isClient = isClient;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setStartWork(String startWork) {
        this.startWork = startWork;
    }
    public void setStartFullTimeWork(String startFullTimeWork) {
        this.startFullTimeWork = startFullTimeWork;
    }
    public void setStopWork(String stopWork) {
        this.stopWork = stopWork;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setPhoneDesk(String phoneDesk) {
        this.phoneDesk = phoneDesk;
    }
    public void setSkype(String skype) {
        this.skype = skype;
    }
    public void setIrc(String irc) {
        this.irc = irc;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setTasksLink(String tasksLink) {
        this.tasksLink = tasksLink;
    }
    public void setAvailabilityLink(String availabilityLink) {
        this.availabilityLink = availabilityLink;
    }
    
    public void setRoles(String roles) {
        this.roles = GsonProvider.get().fromJson(roles, listStringType);
    }
    public void setGroups(String groups) {
        this.groups = GsonProvider.get().fromJson(groups, listStringType);;
    }
    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }
    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }
    
    
    
}
