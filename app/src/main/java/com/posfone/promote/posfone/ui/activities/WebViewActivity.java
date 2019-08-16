package com.posfone.promote.posfone.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.ui.interfaces.WebViewInterface;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    JavaScriptInterface  JSInterface;
    WebView myWebView;
    ProgressBar progressBar;
    String data;
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
       data =  getIntent().getStringExtra("data");
        initViews();
    }

    private void initViews()
    {
        String stripeurl = getIntent().getStringExtra("stripeurl");
        stripeurl = "https://dashboard.stripe.com/oauth/authorize?response_type=code&client_id=ca_Cc0qF3AKZqT8F2rcaXaGkfrtcy5tUxUl&scope=read_write&redirect_uri=https://accounts.protechgenie.in/stripereturn&state=eyJ1c2VyX2lkIjoyMzUsImdhdGV3YXlfaWQiOjEsInJ1cmkiOiJodHRwczpcL1wvYWNjb3VudHMucHJvdGVjaGdlbmllLmluYXBwXC9zdHJpcGVyZXNwb25zZSJ9";
        myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        JSInterface = new JavaScriptInterface(this);
        progressBar=findViewById(R.id.progress);
        progressBar.setMax(100);
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        try{
        myWebView.addJavascriptInterface(new WebViewInterface(this), "Android");}catch (Exception e){e.printStackTrace();}

        /*if (data != null)
        myWebView.loadData(data, "text/html", "UTF-8");
        else*/
            myWebView.loadUrl(data);
        myWebView.setWebChromeClient(new WebChromeClient()  {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        progressBar.setProgress(newProgress);
    }
    });
    }

    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            Log.i("WebViewInterface","showAndroidToast(String toast)");
            Intent intent = new Intent(mContext,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        }
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
