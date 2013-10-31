package com.stxnext.management.android.dto.local;

public class UserProperty {

    String name;
    String value;
    
    public UserProperty(String name, String value){
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
