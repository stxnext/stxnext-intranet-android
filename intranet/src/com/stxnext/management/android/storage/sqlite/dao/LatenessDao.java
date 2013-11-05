package com.stxnext.management.android.storage.sqlite.dao;


public class LatenessDao  extends AbstractDAO{
    
    public interface LatenessColumns{
        public static final String TABLE = "absence";
        
        public static final String AVATAR_URL = "avatar_url";
        public static final String LATE_ID = "late_id";
        public static final String START = "start";
        public static final String END = "end";
        public static final String NAME = "name";
        public static final String EXPLANATION = "explanation";
        public static final String WORK_FROM_HOME = "work_from_home";
    }
    
    
    
}
