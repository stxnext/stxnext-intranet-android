
package com.stxnext.management.android.web.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.AbstractHttpMessage;

import android.util.Log;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.api.HTTPResponse;
import com.stxnext.management.android.web.api.HttpErrorResolver;

public abstract class AbstractService {

    private final static String TAG = "AbstractService";
    
    protected HttpErrorResolver errorResolver;
    protected WebServiceState serviceState;
    protected StoragePrefs prefs;
    
    protected abstract String getServiceDomain();
    
    protected AbstractService(){
        errorResolver = new HttpErrorResolver();
        serviceState = new WebServiceState();
        prefs = StoragePrefs.getInstance(AppIntranet.getApp());
    }

    @SuppressWarnings("rawtypes")
    protected HttpResponse executeRequestAndParseError(HttpUriRequest request,
            final HTTPResponse result) throws Exception {
        serviceState.getClient().getConnectionManager().closeExpiredConnections();
        HttpResponse response = serviceState.getClient().execute(request, serviceState.getLocalContext());
        if (result != null) {
//            if (response.containsHeader("Location")) {
//                Header[] locations = response.getAllHeaders();
//                if (locations.length > 0){
//                    Log.e("redirect to",locations[0].getValue());
//                }
//                    //context.setAttribute(LAST_REDIRECT_URL, locations[0].getValue());
//            }
            result.setError(errorResolver.resolve(response));
        }

        return response;
    }
    
    public void clearCookies(){
        serviceState.clearCookies();
    }
    
    protected void saveCookies(){
        serviceState.saveCookies();
    }
    
    public enum RequestType {
        POST, GET, PUT, DELETE
    }

    public enum RequestHeader {
        JSON("application/json");

        public static final String HEADER_ACCEPT = "Accept";
        public static final String HEADER_CONTENT_TYPE = "Content-Type";
        private final String acceptHeader;

        RequestHeader(String acceptHeader) {
            this.acceptHeader = acceptHeader;
        }

        public String getAcceptHeader() {
            return this.acceptHeader;
        }
    }

    protected byte[] getEntityBytesAndConsume(HttpEntity entity) throws IllegalStateException,
            IOException {
        byte[] bytes = null;
        InputStream stream = entity.getContent();
        bytes = IOUtils.toByteArray(stream);
        stream.close();
        return bytes;
    }

    private String resolveUrl(String path) {
        return String.format("https://%s/%s",
                getServiceDomain(),
                path);
    }

    protected void setAcceptHeader(AbstractHttpMessage message, RequestHeader header) {
        message.setHeader(RequestHeader.HEADER_ACCEPT, header.getAcceptHeader());
    }
    
    protected void setContentType(AbstractHttpMessage message, RequestHeader header){
        message.setHeader(RequestHeader.HEADER_CONTENT_TYPE, header.getAcceptHeader());
    }
    
    protected void setAuthHeaders(AbstractHttpMessage message){
        StringBuilder sb = new StringBuilder();
        for(Cookie cookie : prefs.getCookies()){
            sb.append(cookie.getName()).append("=");
            sb.append(cookie.getValue());
            sb.append(";");
        }
        message.addHeader("Cookie", sb.toString());
    }
    
    protected void setRequiredExtraHeaders(AbstractHttpMessage message){
        message.setHeader("X-Requested-With","XMLHttpRequest");
    }

    protected HttpPost postRequest(String path,
            List<NameValuePair> dictionary) {
        String url = resolveUrl(path);
        HttpPost request = new HttpPost(url);
        setRequiredExtraHeaders(request);
        if (dictionary != null) {
            try {
                request.setEntity(new UrlEncodedFormEntity(dictionary));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG,"",e);
            }
        }
        request.setParams(serviceState.getClient().getParams());
        return request;
    }

    protected HttpPut putRequest(String path,
            List<NameValuePair> dictionary) {

        String url = resolveUrl(path);

        HttpPut request = new HttpPut(url);
        setRequiredExtraHeaders(request);
        if (dictionary != null) {
            try {
                request.setEntity(new UrlEncodedFormEntity(dictionary));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG,"",e);
            }
        }
        request.setParams(serviceState.getClient().getParams());
        return request;
    }

    protected HttpDelete deleteRequest(String path,
            List<NameValuePair> dictionary) {
        String url = resolveUrl(path);

        if (dictionary != null) {
            String paramString = "?" + URLEncodedUtils.format(dictionary, "utf-8");
            url = url + paramString;
        }

        HttpDelete request = new HttpDelete(url);
        setRequiredExtraHeaders(request);
        request.setParams(serviceState.getClient().getParams());
        return request;
    }

    protected HttpGet getRequest(String path,
            List<NameValuePair> dictionary, boolean doNotEncode) {
        String url = resolveUrl(path);

        if (dictionary != null) {
            url = url + getQueryParamsFromNameValuePairs(dictionary, doNotEncode);
        }

        HttpGet request = new HttpGet(url);
        setRequiredExtraHeaders(request);
        request.setParams(serviceState.getClient().getParams());
        return request;
    }

    protected String getQueryParamsFromNameValuePairs(List<NameValuePair> dictionary,
            boolean doNotEncode) {
        String paramString = "?";

        if (doNotEncode) {
            for (NameValuePair pair : dictionary) {
                paramString += pair.getName() + "=" + pair.getValue() + "&";
            }
            paramString = paramString.substring(0, paramString.length() - 1);
        }
        else {
            for (NameValuePair pair : dictionary) {
                paramString += pair.getName() + "=" + URLEncoder.encode(pair.getValue()) + "&";
            }
        }

        return paramString;
    }
    
    protected void consume(HttpEntity entity) {
        if (entity != null) {
            try {
                entity.consumeContent();
            } catch (Exception e) {
                Log.e("service","entity consumption issues",e);
            }
        }
    }

}
