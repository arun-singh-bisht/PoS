package com.posfone.promote.posfone.ui.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.posfone.promote.posfone.ui.activities.MainActivity;


public class WebViewInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        public WebViewInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            Log.i("WebViewInterface","showAndroidToast(String toast)");
            Intent intent = new Intent(mContext,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            ((Activity)mContext).finish();
        }

       /* *//** Show a toast from the web page *//*
        @JavascriptInterface
        public void showToast() {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            Log.i("WebViewInterface","showToast()");
        }*/
    }
