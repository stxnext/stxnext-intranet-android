
package com.stxnext.management.android.web.api;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.ParseException;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.util.EntityUtils;

public class HttpErrorResolver {

    private final static String JSON_ERROR_MESSAGE_KEY = "message";
    private final static String TAG = "HttpErrorResolver";

    public HTTPError resolve(HttpResponse response) throws ParseException, IOException {
        HTTPError error = null;

        StatusLine status = response.getStatusLine();
        if (status == null) {
        }
        else {
            int code = status.getStatusCode();
            String message = null;
            if (code >= 300) {
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                Log.e("FAILED RESPONSE ENTITY:", content);
                try {
                    JSONObject jsonResponse = new JSONObject(content);
                    if (jsonResponse.has(JSON_ERROR_MESSAGE_KEY)) {
                        message = jsonResponse.getString(JSON_ERROR_MESSAGE_KEY);
                        error = new HTTPError(code, message);
                    }
                } catch (JSONException jex) {
                    Log.e(TAG, "", jex);
                }

                if (error == null) {
                    error = applyStandarizedError(code);
                }
            }
        }

        return error;
    }

    private HTTPError applyStandarizedError(int code) {

        String messageFormat = "Error %d: %s";
        String message = null;

        switch (code) {
            case 300 | 301 | 302 | 303 | 304:
                message = String.format(messageFormat, code, "Redirection.");
                break;
            case 400:
                message = String.format(messageFormat, code,
                        "Bad Request.");
                break;

            case 401:
                message = String.format(messageFormat, code, "Unauthorized.");

            case 402:
                message = String.format(messageFormat, code, "Payment requred.");
                break;
            case 403:
                message = String
                        .format(messageFormat,
                                code,
                                "Forbidden. You do not have access to this resource.");
                break;

            case 404:
                message = String.format(messageFormat, code, "Resource was not found.");
                break;
            case 405:
                message = String.format(messageFormat, code,
                        "Method is not allowed.");
                break;
            case 408:
                message = String.format(messageFormat, code,
                        "Request timeout.");
                break;
            case 409:
                message = String.format(messageFormat, code, "Conflict between client and server.");
                break;
            case 410:
                message = String.format(messageFormat, code,
                        "That resource is no longer available.");
                break;
            case 411:
                message = String.format(messageFormat, code,
                        "Length required for this request.");
                break;
            case 429:
                message = String.format(messageFormat, code, "Too many requests.");
                break;
            case 444:
                message = String.format(messageFormat, code,
                        "The server returned no response.");
                break;
            case 500:
                message = String.format(messageFormat, code,
                        "Internal server error.");
                break;
            case 501:
                message = String.format(messageFormat, code,
                        "That resource is not implemented.");
                break;
            case 502:
                message = String.format(messageFormat, code,
                        "Bad gateway.");
                break;
            case 503:
                message = String.format(messageFormat, code,
                        "Service is currently unavailable. Please check again later.");
                break;
            case 504:
                message = String.format(messageFormat, code,
                        "Gateway timeout.");
                break;
            case 505:
                message = String.format(messageFormat, code,
                        "HTTP Version not supported.");
                break;

            default:
                message = String.format(messageFormat, code, "Unexpected error");
                break;
        }

        return new HTTPError(code, message);
    }

}
