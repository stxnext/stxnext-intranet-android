
package com.stxnext.management.android.storage.prefs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import ch.boye.httpclientandroidlib.cookie.Cookie;

import com.google.common.base.Strings;
import com.stxnext.management.android.dto.local.CookiesHolder;
import com.stxnext.management.android.dto.local.LocalCookie;

public class StoragePrefs {

    public static final String PREFS_LOCATION = "com.stxnext";

    private static final String STRING_WEB_COOKIES = "stringWebCookies";
    private static final String AUTH_CODE = "authCode";
    private static final String SYNC_IS_ON = "syncIsOn";

    private static StoragePrefs _instance;
    private Context context;
    private SharedPreferences prefs;

    public static final StoragePrefs getInstance(Context context) {
        if (_instance == null) {
            _instance = new StoragePrefs(context);
            _instance.setSyncing(false);
        }
        return _instance;
    }

    private StoragePrefs(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS_LOCATION, Context.MODE_MULTI_PROCESS);
    }

    public void setCookies(List<Cookie> cookies) {
        if (cookies == null) {
            prefs.edit().putString(STRING_WEB_COOKIES, null).commit();
            return;
        }

        ArrayList<LocalCookie> locals = new ArrayList<LocalCookie>();
        for (Cookie c : cookies) {
            locals.add(new LocalCookie(c));
        }
        CookiesHolder holder = new CookiesHolder();
        holder.setCookies(locals);
        String jsonForm = holder.serialize();
        prefs.edit().putString(STRING_WEB_COOKIES, jsonForm).commit();
    }

    public List<Cookie> getCookies() {
        List<Cookie> result = new ArrayList<Cookie>();
        String jsonForm = prefs.getString(STRING_WEB_COOKIES, null);
        if (!Strings.isNullOrEmpty(jsonForm)) {
            CookiesHolder holder = CookiesHolder.fromJsonString(jsonForm, CookiesHolder.class);
            if (holder != null) {
                for (LocalCookie local : holder.getCookies()) {
                    result.add(local.convertToRealCookie());
                }
            }
        }
        return result;
    }

    public void setAuthCode(String code) {
        prefs.edit().putString(AUTH_CODE, code).commit();
    }

    public String getAuthCode() {
        return prefs.getString(AUTH_CODE, null);
    }

    public void setSyncing(boolean syncing) {
        prefs.edit().putBoolean(SYNC_IS_ON, syncing).commit();
    }

    public boolean isSyncing() {
        return prefs.getBoolean(SYNC_IS_ON, false);
    }

}
