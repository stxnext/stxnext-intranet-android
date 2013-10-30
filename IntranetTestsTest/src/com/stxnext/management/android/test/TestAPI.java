package com.stxnext.management.android.test;

import android.test.ApplicationTestCase;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.api.IntranetApi;

public class TestAPI  extends ApplicationTestCase<AppIntranet>{

    private IntranetApi api;
    private AppIntranet app;
    private StoragePrefs prefs;
    
    public TestAPI() {
        super(AppIntranet.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        app = AppIntranet.getApp();
        api = IntranetApi.getInstance(app);
        prefs = StoragePrefs.getInstance(app);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
 
    public void testUsersList(){
        String code = prefs.getAuthCode();
        api.loginWithCode(code);
        api.getUsers("");
    }
    
}
