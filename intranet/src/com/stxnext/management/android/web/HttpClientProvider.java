
package com.stxnext.management.android.web;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ch.boye.httpclientandroidlib.HttpVersion;
import ch.boye.httpclientandroidlib.NoHttpResponseException;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.HttpRequestRetryHandler;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.conn.params.ConnManagerParams;
import ch.boye.httpclientandroidlib.conn.params.ConnPerRoute;
import ch.boye.httpclientandroidlib.conn.routing.HttpRoute;
import ch.boye.httpclientandroidlib.conn.scheme.PlainSocketFactory;
import ch.boye.httpclientandroidlib.conn.scheme.Scheme;
import ch.boye.httpclientandroidlib.conn.scheme.SchemeRegistry;
import ch.boye.httpclientandroidlib.conn.ssl.SSLSocketFactory;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.tsccm.ThreadSafeClientConnManager;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.params.HttpProtocolParams;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

public class HttpClientProvider {

    private HttpClient providedClient;
    
    static HttpClientProvider _instance;

    public static final int TIMEOUT_CONNECTION_ESTABLISH = 15000;
    public static final int TIMEOUT_CONNECTION_WAIT_FOR_DATA = 35000;

    private HttpClientProvider(){
    }
    
    public static HttpClientProvider getInstance(){
        if(_instance==null){
            _instance = new HttpClientProvider();  
        }
        return _instance;
    }
    
    public HttpClient get() {
        if (providedClient == null) {
            providedClient = getNewHttpClient();
        }
        return providedClient;
    }

    private DefaultHttpClient applyTimeout(DefaultHttpClient httpClient) {
        HttpParams httpParameters = httpClient.getParams();
        if (httpParameters == null) {
            httpParameters = new BasicHttpParams();
        }
        HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION_ESTABLISH);
        HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_CONNECTION_WAIT_FOR_DATA);

        httpParameters.setParameter("http.tcp.nodelay", true);
        httpParameters.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

        httpClient.setParams(httpParameters);

        return httpClient;
    }

    private HttpClient getNewHttpClient() {

        DefaultHttpClient client = null;

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new TrustedSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();

            ConnManagerParams.setMaxTotalConnections(params, 1000);

            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, false);
            HttpConnectionParams.setStaleCheckingEnabled(params, false);
            HttpConnectionParams.setSocketBufferSize(params, 8192);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRoute() {
                @Override
                public int getMaxForRoute(HttpRoute route) {
                    return 35;
                }
            });

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            client = new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            client = new DefaultHttpClient();
        }

        HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {

            public boolean retryRequest(IOException exception, int executionCount,
                    HttpContext context) {
                // retry a max of 5 times
                if (executionCount >= 3) {
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {
                    return true;
                } else if (exception instanceof ClientProtocolException) {
                    return true;
                }
                else if (exception instanceof SSLException) {
                    return true;
                }

                return false;
            }
        };
        client.setHttpRequestRetryHandler(retryHandler);

        client = applyTimeout(client);
        return client;
    }

    private class TrustedSSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public TrustedSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException,
                KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] {
                    tm
            }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

}
