package com.stxnext.management.android.test;

import java.util.List;

import android.test.ApplicationTestCase;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.dto.local.Team;
import com.stxnext.management.android.dto.local.TeamResult;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.storage.sqlite.dao.DAO;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.IntranetApi;

public class TestAPI  extends ApplicationTestCase<AppIntranet>{

    private IntranetApi api;
    private AppIntranet app;
    private StoragePrefs prefs;
    private DAO dao;
    
    public TestAPI() {
        super(AppIntranet.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        app = AppIntranet.getApp();
        api = IntranetApi.getInstance(app);
        prefs = StoragePrefs.getInstance(app);
        dao = DAO.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
 
    public void testUsersList(){
//        String code = prefs.getAuthCode();
//        api.loginWithCode(code);
        //api.getUsers();
        HTTPResponse<TeamResult> result = api.getTeams(null);
        dao.getTeam().persist(result.getExpectedResponse().getTeams());
        List<Team> dbTeams = dao.getTeam().fetch();
        dao.getTeam().clear();
        dbTeams = dao.getTeam().fetch();
        
        String breakPt = "";
    }
    
}
