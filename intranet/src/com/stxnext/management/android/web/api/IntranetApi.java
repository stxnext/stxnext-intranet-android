
package com.stxnext.management.android.web.api;

import android.app.Application;
import android.graphics.Bitmap;

import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.dto.local.PresenceResult;
import com.stxnext.management.android.dto.local.postmessage.LateMessage;
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

    public void clearCookies() {
        service.clearCookies();
    }

    public HTTPResponse<IntranetUsersResult> getUsers() {
        HTTPResponse<IntranetUsersResult> result = call(false,
                new ApiExecutable<HTTPResponse<IntranetUsersResult>>() {
                    @Override
                    public HTTPResponse<IntranetUsersResult> call() throws Exception {
                        return service.getUsers();
                    }
                });
        return result;
    }

    public HTTPResponse<PresenceResult> getPresences() {
        HTTPResponse<PresenceResult> result = call(false,
                new ApiExecutable<HTTPResponse<PresenceResult>>() {
                    @Override
                    public HTTPResponse<PresenceResult> call() throws Exception {
                        return service.getPresences();
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

    public Bitmap downloadBitmap(final String url) {
        Bitmap result = call(false,
                new ApiExecutable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        return service.downloadBitmap(url);
                    }
                });
        return result;
    }
    
    public HTTPResponse<Boolean> submitLateness(final LateMessage messagge){
        HTTPResponse<Boolean> result = call(false,
                new ApiExecutable<HTTPResponse<Boolean>>() {
                    @Override
                    public HTTPResponse<Boolean> call() throws Exception {
                        return service.submitLateness(messagge);
                    }
                });
        return result;
    }

}
