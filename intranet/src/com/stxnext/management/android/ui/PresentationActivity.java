package com.stxnext.management.android.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.stxnext.management.android.R;

public class PresentationActivity  extends AbstractSimpleActivity {

    private static final String URL = "http://stxnext.pl";

    private WebView webView;

    @Override
    protected void fillViews() {
        this.webView = (WebView) findViewById(R.id.webView);
        this.webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    protected void applyWindowSettings() {
        super.applyWindowSettings();
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CookieManager.getInstance().setAcceptCookie(true);
    }
    
    @Override
    protected void setActions() {

    WebViewClient client = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                setProgressBarIndeterminateVisibility(true);
                super.onPageStarted(view, url, favicon);
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                setProgressBarIndeterminateVisibility(false);
            }
        };
    
        webView.setWebViewClient(client);
        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(URL);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getContentResourceId() {
        return R.layout.activity_presentation;
    }

}
