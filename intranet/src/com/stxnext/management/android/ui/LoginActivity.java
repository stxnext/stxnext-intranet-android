
package com.stxnext.management.android.ui;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.stxnext.management.android.R;

public class LoginActivity extends AbstractSimpleActivity {

    private static final String LOGIN_URL = "https://accounts.google.com/o/oauth2/auth?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar.readonly&redirect_uri=https%3A%2F%2Fintranet.stxnext.pl%2Fauth%2Fcallback&response_type=code&client_id=83120712902.apps.googleusercontent.com&access_type=offline";

    public static final int RESULT_SIGNED_IN = 2;
    public static final int RESULT_CANCELLED = 3;
    
    private WebView webView;

    @Override
    protected void fillViews() {
        this.webView = (WebView) findViewById(R.id.webView);
    }

    @Override
    protected void setActions() {

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                
                String cookies = CookieManager.getInstance().getCookie("https://intranet.stxnext.pl");
                Log.e("","url:"+url);
                if(url.contains("code=")){
                    webView.stopLoading();
                    Log.e("","GOT CODE:");
                    Log.e("","got cookie:"+cookies);
                    String code = url.substring(url.indexOf("code=")+"code=".length());
                    prefs.setAuthCode(code);
                    
                    CookieManager.getInstance().removeAllCookie();
                    
                    setResult(RESULT_SIGNED_IN);
                    finish();
                }
            }
        });

        webView.loadUrl(LOGIN_URL);
    }
    
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_login;
    }

}
