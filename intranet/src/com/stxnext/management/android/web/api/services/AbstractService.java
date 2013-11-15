
package com.stxnext.management.android.web.api.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.io.IOUtils;

import android.util.Log;
import ch.boye.httpclientandroidlib.Header;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpDelete;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpPut;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.client.utils.URLEncodedUtils;
import ch.boye.httpclientandroidlib.message.AbstractHttpMessage;

import com.stxnext.management.android.AppIntranet;
import com.stxnext.management.android.storage.prefs.StoragePrefs;
import com.stxnext.management.android.web.api.HttpErrorResolver;
import com.stxnext.management.android.web.api.HTTPResponse;

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
        JSON("application/json; charset=UTF-8");

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
    

    protected HttpPost postRequest(String path,
            List<NameValuePair> dictionary) {
        String url = resolveUrl(path);
        HttpPost request = new HttpPost(url);
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

}
