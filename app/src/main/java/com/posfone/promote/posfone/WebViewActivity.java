package com.posfone.promote.posfone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.posfone.promote.posfone.interfaces.WebViewInterface;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {


    WebView myWebView;
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initViews();
    }


    private void initViews()
    {

        String stripeurl = getIntent().getStringExtra("stripeurl");
        stripeurl = "https://dashboard.stripe.com/oauth/authorize?response_type=code&client_id=ca_Cc0qF3AKZqT8F2rcaXaGkfrtcy5tUxUl&scope=read_write&redirect_uri=https://accounts.protechgenie.in/stripereturn&state=eyJ1c2VyX2lkIjoyMzUsImdhdGV3YXlfaWQiOjEsInJ1cmkiOiJodHRwczpcL1wvYWNjb3VudHMucHJvdGVjaGdlbmllLmluYXBwXC9zdHJpcGVyZXNwb25zZSJ9";
        myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.addJavascriptInterface(new WebViewInterface(this), "Android");
        myWebView.loadUrl(stripeurl);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

}
