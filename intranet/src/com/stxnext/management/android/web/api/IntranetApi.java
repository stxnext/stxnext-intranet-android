
package com.stxnext.management.android.web.api;

import android.app.Application;
import android.graphics.Bitmap;

import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.dto.local.MandatedTime;
import com.stxnext.management.android.dto.local.PresenceResult;
import com.stxnext.management.android.dto.postmessage.AbsencePayload;
import com.stxnext.management.android.dto.postmessage.LatenessPayload;

public class IntranetApi extends AbstractApi {

    private static IntranetApi _instance;

    public static IntranetApi getInstance(Application app) {
        if (_instance == null) {
            _instance = new IntranetApi(app);
        }
        return _instance;
    }

    private IntranetApi(Application app) {
        super(app);
    }

    public void clearCookies() {
        service.clearCookies();
    }

    public HTTPResponse<IntranetUsersResult> getUsers() {
        HTTPResponse<IntranetUsersResult> result = call(false,
                new ApiExecutable<IntranetUsersResult>() {
                    @Override
                    public HTTPResponse<IntranetUsersResult> call() throws Exception {
                        return service.getUsers();
                    }
                });
        return result;
    }

    public HTTPResponse<PresenceResult> getPresences() {
        HTTPResponse<PresenceResult> result = call(false,
                new ApiExecutable<PresenceResult>() {
                    @Override
                    public HTTPResponse<PresenceResult> call() throws Exception {
                        return service.getPresences();
                    }
                });
        return result;
    }

    public HTTPResponse<String> loginWithCode(final String code) {
        HTTPResponse<String> result = call(false,
                new ApiExecutable<String>() {
                    @Override
                    public HTTPResponse<String> call() throws Exception {
                        return service.loginWithCode(code);
                    }
                });
        return result;

    }

    public HTTPResponse<Bitmap> downloadBitmap(final String url) {
        HTTPResponse<Bitmap> result = call(false,
                new ApiExecutable<Bitmap>() {
                    @Override
                    public HTTPResponse<Bitmap> call() throws Exception {
                        return service.downloadBitmap(url);
                    }
                });
        return result;
    }

    public HTTPResponse<Boolean> submitLateness(final LatenessPayload messagge) {
        HTTPResponse<Boolean> result = call(false,
                new ApiExecutable<Boolean>() {
                    @Override
                    public HTTPResponse<Boolean> call() throws Exception {
                        return service.submitLateness(messagge);
                    }
                });
        return result;
    }

    public HTTPResponse<Boolean> submitAbsence(final AbsencePayload messagge) {
        HTTPResponse<Boolean> result = call(false,
                new ApiExecutable<Boolean>() {
                    @Override
                    public HTTPResponse<Boolean> call() throws Exception {
                        return service.submitAbsence(messagge);
                    }
                });
        return result;
    }

    public HTTPResponse<MandatedTime> getDaysOffToTake() {
        HTTPResponse<MandatedTime> result = call(false,
                new ApiExecutable<MandatedTime>() {
                    @Override
                    public HTTPResponse<MandatedTime> call() throws Exception {
                        return service.getDaysOffToTake();
                    }
                });
        return result;
    }

}
