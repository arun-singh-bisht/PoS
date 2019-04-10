package com.posfone.promote.posfone.Utils;

import android.content.Context;

import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;

public class TwilioTokenManager {

    private Context context;
    private SharedPreferenceHandler sharedPreferenceHandler;
    public TwilioTokenManager(Context context) {
        this.context = context;
        sharedPreferenceHandler = new SharedPreferenceHandler(context);
    }

    public boolean isTokenValid()
    {
        long time = sharedPreferenceHandler.getLongValue(SharedPreferenceHandler.SP_KEY_TWILIO_ACCESS_TOKEN_TIME);
        if(time == 0)
            return false;

        long timeDiff = System.currentTimeMillis() - time;
        if(timeDiff>(1000*60*59))
            return false;

        return true;
    }

    public void saveToken(String token)
    {
        sharedPreferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_TWILIO_ACCESS_TOKEN,token);
        sharedPreferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_TWILIO_ACCESS_TOKEN_TIME,System.currentTimeMillis());
    }

    public String getToken()
    {
        return sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_TWILIO_ACCESS_TOKEN);
    }
}
