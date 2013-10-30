
package com.stxnext.management.android.web.api;

import android.app.Application;

import com.stxnext.management.android.web.api.services.IntranetService;

public class IntranetApi extends AbstractApi {

    private static IntranetApi _instance;

    IntranetService service;

    public static IntranetApi getInstance(Application app) {
        if (_instance == null) {
            _instance = new IntranetApi(app);
        }
        return _instance;
    }

    private IntranetApi(Application app) {
        super(app);
        service = new IntranetService();
    }

    public HTTPResponse<String> getUsers(){
        HTTPResponse<String> result = call(false,
                new ApiExecutable<HTTPResponse<String>>() {
                    @Override
                    public HTTPResponse<String> call() throws Exception {
                        return service.getUsers();
                    }
                });
        return result;
    }
    
    public HTTPResponse<String> loginWithCode(final String code) {
        HTTPResponse<String> result = call(false,
                new ApiExecutable<HTTPResponse<String>>() {
                    @Override
                    public HTTPResponse<String> call() throws Exception {
                        return service.loginWithCode(code);
                    }
                });
        return result;
        
    }

}
