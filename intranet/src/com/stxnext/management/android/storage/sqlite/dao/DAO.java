package com.stxnext.management.android.storage.sqlite.dao;

public class DAO {

    private static DAO _instance;
    
    public static DAO getInstance(){
        if(_instance == null){
            _instance = new DAO();
        }
        return _instance;
    }
    
    IntranetUserDao intranetUser;
    
    private DAO(){
        intranetUser = new IntranetUserDao();
    }

    public IntranetUserDao getIntranetUser() {
        return intranetUser;
    }
}
