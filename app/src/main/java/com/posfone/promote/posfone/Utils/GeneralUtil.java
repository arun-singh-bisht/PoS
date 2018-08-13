package com.posfone.promote.posfone.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class GeneralUtil {

    public static void showToast(Context context, String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }

    public static String getTextFromEditText(View view, int resId)
    {
        return ((EditText)view.findViewById(resId)).getText().toString();
    }
}
