
package com.stxnext.management.android.web.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import android.graphics.Bitmap;
import android.util.Log;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.BufferedHttpEntity;
import ch.boye.httpclientandroidlib.entity.StringEntity;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.stxnext.management.android.dto.local.IntranetUsersResult;
import com.stxnext.management.android.dto.local.MandatedTime;
import com.stxnext.management.android.dto.local.PresenceResult;
import com.stxnext.management.android.dto.postmessage.AbsenceMessage;
import com.stxnext.management.android.dto.postmessage.AbstractMessage;
import com.stxnext.management.android.dto.postmessage.GsonProvider;
import com.stxnext.management.android.dto.postmessage.LatenessMessage;
import com.stxnext.management.android.ui.dependencies.BitmapUtils;
import com.stxnext.management.android.web.api.HTTPError;
import com.stxnext.management.android.web.api.HTTPResponse;

public class IntranetService extends AbstractService {

    @Override
    protected String getServiceDomain() {
        return "intranet.stxnext.pl";
    }

    public IntranetService() {
        super();
    }

    public HTTPResponse<Bitmap> downloadBitmap(String url) {
        HttpGet request = new HttpGet(url);
        HTTPResponse<Bitmap> result = new HTTPResponse<Bitmap>();
        try {
            serviceState.getClient().getConnectionManager().closeExpiredConnections();
            HttpResponse response;
            response = serviceState.getClient().execute(request, serviceState.getLocalContext());

            if (response.getStatusLine().getStatusCode() != 200) {
                result.setError(new HTTPError(response.getStatusLine().getStatusCode(), response
                        .getStatusLine().toString()));
                return result;
            }
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(response.getEntity());
            InputStream instream = bufHttpEntity.getContent();
            result.setExpectedResponse(BitmapUtils.decodeSampledBitmapFromResource(instream, 0, 0));
            instream.close();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public HTTPResponse<String> loginWithCode(String code)
            throws Exception {

        HTTPResponse<String> result = new HTTPResponse<String>();
        HttpGet request = getRequest("auth/callback?code=" + code, null, false);

        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            saveCookies();
            String jsonStub = EntityUtils.toString(entity);
            Log.e("", "ok");
        }
        EntityUtils.consume(entity);
        return result;
    }

    public HTTPResponse<PresenceResult> getPresences()
            throws Exception {

        HTTPResponse<PresenceResult> result = new HTTPResponse<PresenceResult>();
        HttpGet request = getRequest("api/presence", null, false);
        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            String jsonStub = EntityUtils.toString(entity);
            PresenceResult users = PresenceResult.fromJsonString(jsonStub,
                    PresenceResult.class);
            result.setExpectedResponse(users);
            saveCookies();
        }
        EntityUtils.consume(entity);
        return result;
    }

    public HTTPResponse<IntranetUsersResult> getUsers()
            throws Exception {

        HTTPResponse<IntranetUsersResult> result = new HTTPResponse<IntranetUsersResult>();
        HttpGet request = getRequest("api/users?full=1&inactive=0", null, false);
        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            String jsonStub = EntityUtils.toString(entity);
            IntranetUsersResult users = IntranetUsersResult.fromJsonString(jsonStub,
                    IntranetUsersResult.class);
            result.setExpectedResponse(users);
            saveCookies();
        }
        EntityUtils.consume(entity);
        return result;
    }

    public HTTPResponse<MandatedTime> getDaysOffToTake()
            throws Exception {

        String methodToCall = String.format("api/absence_days?date_start=%s&type=planowany",
                AbstractMessage.defaultDateFormat.format(new Date()));

        HTTPResponse<MandatedTime> result = new HTTPResponse<MandatedTime>();
        HttpGet request = getRequest(methodToCall, null, false);
        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            String jsonStub = EntityUtils.toString(entity);
            result.setExpectedResponse(GsonProvider.get().fromJson(jsonStub, MandatedTime.class));
        }
        EntityUtils.consume(entity);
        return result;
    }

    public HTTPResponse<Boolean> submitLateness(LatenessMessage messagge)
            throws Exception {

        HTTPResponse<Boolean> result = new HTTPResponse<Boolean>();
        HttpPost request = postRequest("api/lateness", null);

        StringEntity postEntity = new StringEntity(messagge.toString());
        request.setEntity(postEntity);

        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            String jsonStub = EntityUtils.toString(entity);
            Log.e("submit lateness response",jsonStub);
        }
        EntityUtils.consume(entity);
        return result;
    }

    public HTTPResponse<Boolean> submitAbsence(AbsenceMessage messagge)
            throws Exception {

        HTTPResponse<Boolean> result = new HTTPResponse<Boolean>();
        HttpPost request = postRequest("api/absence", null);

        StringEntity postEntity = new StringEntity(messagge.toString());
        request.setEntity(postEntity);

        HttpResponse response = executeRequestAndParseError(request, result);
        HttpEntity entity = response.getEntity();
        if (result.ok()) {
            String jsonStub = EntityUtils.toString(entity);
            Log.e("getDaysOffToTake",jsonStub);
        }
        EntityUtils.consume(entity);
        return result;
    }

}
