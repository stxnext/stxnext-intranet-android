
package com.stxnext.management.android.games.poker.multiplayer.parse;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseUser;
import com.parse.PushService;
import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.ui.MainActivity;

public class ParseUtils {

    public static final String USER_CHANNEL_PREFIX = "user_channel_";

    private static String _userChannel;

    private static ParseUtils _instance;

    private ParseUtils() {
    }

    public static ParseUtils getInstace() {
        if (_instance == null) {
            _instance = new ParseUtils();
        }
        return _instance;
    }

    public static String getUserChannel() {
        return _userChannel;
    }

    public static boolean registerForChannelUpdates(Context context) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            _userChannel = USER_CHANNEL_PREFIX + user.getObjectId();
            PushService.subscribe(context, _userChannel, MainActivity.class);
            Log.e("", "registered for user channel: " + _userChannel);
            return true;
        }
        return false;
    }

    public static Exception sendPush(Context context, String channelId) {
        JSONObject json;
        try {
            json = new JSONObject(
                    "{\"action\":\"net.smarterdevice.utils.languagehelper.service.SYNC\",\"cdata\":\""
                            + channelId + "\"}");
        } catch (JSONException e) {
            return e;
        }
        List<String> channels = new ArrayList<String>();
        if (_userChannel == null) {
            registerForChannelUpdates(context);
        }
        channels.add(_userChannel);

        HttpURLConnection urlConnection = null;
        Exception result = null;
        try {
            URL url = new URL("https://api.parse.com/1/push");
            urlConnection = (HttpURLConnection) url.openConnection();
            JSONObject finalJson = new JSONObject();
            finalJson.put("channels", new JSONArray(channels));
            finalJson.put("data", json);

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("X-Parse-Application-Id",
                    AppIntranet.APP_ID);
            urlConnection.setRequestProperty("X-Parse-REST-API-Key",
                    AppIntranet.REST_API_KEY);
            urlConnection
                    .setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);

            OutputStreamWriter osw = new OutputStreamWriter(
                    urlConnection.getOutputStream());
            osw.write(finalJson.toString());
            osw.flush();
            osw.close();

            int response = urlConnection.getResponseCode();
            String message = urlConnection.getResponseMessage();
            Log.e("", "response: " + response + "/message:" + message);
        } catch (Exception ex) {
            // /Log.e("","",ex);
            result = ex;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return result;
    }

    public abstract class PushCallback {
        public abstract void onFinish(Exception ex);
    }

    public void PushSyncRequest(PushCallback callback, Context context, String channelId) {
        new PushTask(callback, context, channelId).execute();
    }

    private class PushTask extends AsyncTask<Void, Void, Exception> {
        PushCallback callback;
        Context context;
        String channelId;

        public PushTask(PushCallback callback, Context context, String channelId) {
            this.callback = callback;
            this.context = context;
            this.channelId = channelId;
        }

        @Override
        protected Exception doInBackground(Void... params) {
            Exception result = null;
            result = ParseUtils.sendPush(context, channelId);
            return result;
        }

        @Override
        protected void onPostExecute(Exception result) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.onFinish(result);
            }
        }

    }

}
