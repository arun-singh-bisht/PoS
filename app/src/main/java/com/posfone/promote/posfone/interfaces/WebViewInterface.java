package com.posfone.promote.posfone.interfaces;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;



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
            Log.i("WebViewInterface","showToast(String toast)");
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast() {
            //Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
            Log.i("WebViewInterface","showToast()");
        }
    }
