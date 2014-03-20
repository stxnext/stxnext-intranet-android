package com.stxnext.management.android.storage.sqlite.dao;

public interface TeamColumns {

    public static String TABLE = "team";
    
    public static String EXTERNAL_ID = "external_id";
    public static String IMAGE = "img";
    public static String NAME = "name";
    
    public interface TeamUserColumns{
        public static String TABLE = "team_user";
        
        public static String TEAM_ID = "team_id";
        public static String USER_ID = "user_id";
    }
}
