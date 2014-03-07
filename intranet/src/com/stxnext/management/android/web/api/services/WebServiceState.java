
package com.stxnext.management.android.web.api.services;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.HttpClientProvider;

public class WebServiceState  {

    private final HttpContext localContext;
    private final CookieStore cookieStore;
    private HttpClient client;
    private StoragePrefs prefs;
    
    public WebServiceState() {
        cookieStore = new BasicCookieStore();
        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        client = HttpClientProvider.getInstance().get();
        prefs = StoragePrefs.getInstance(AppIntranet.getApp());
        loadCookies();
    }

    private void loadCookies(){
        prefs.getCookies();
        for(Cookie c : prefs.getCookies()){
            cookieStore.addCookie(c);
        }
    }
    
    public void clearCookies(){
        cookieStore.clear();
    }
    
    public void saveCookies(){
        prefs.setCookies(cookieStore.getCookies());
    }
    
    public HttpContext getLocalContext() {
        return localContext;
    }
    
    public HttpClient getClient() {
        return client;
    }

    public void clearCookiesFromClientCookieStore() {
        this.cookieStore.clear();
    }

}
