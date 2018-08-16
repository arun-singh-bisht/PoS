package com.posfone.promote.posfone.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHandler {


    public static String SP_KEY_USER_ID = "user_id";
    public static String SP_KEY_USER_NAME = "user_name";
    public static String SP_KEY_SIGN_UP_STEP_2 = "sign_up_step_2";
    public static String SP_KEY_SIGN_UP_STEP_3 = "sign_up_step_3";
    public static String SP_KEY_SIGN_UP_STEP_4 = "sign_up_step_4";


    private SharedPreferences sharedPreferences;

    public SharedPreferenceHandler(Context context) {

        sharedPreferences = context.getSharedPreferences("PoSfone",0);
    }

    public void clearSP()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }

    public void putValue(String key,String value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value).commit();
    }

    public void putValue(String key,boolean value)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public String getStringValue(String key)
    {
        return sharedPreferences.getString(key,null);
    }

    public boolean getBooleanValue(String key)
    {
        return sharedPreferences.getBoolean(key,false);
    }

}
