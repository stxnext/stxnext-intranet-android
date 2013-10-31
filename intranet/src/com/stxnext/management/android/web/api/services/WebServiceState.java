
package com.stxnext.management.android.web.api.services;

import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

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
