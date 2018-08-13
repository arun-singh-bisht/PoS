package com.posfone.promote.posfone.sip;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;


public class SipController  implements SipRegistrationListener {

    public  SipManager mSipManager = null;
    public SipProfile mSipProfile = null;
    public Context context;
    public static String SIP_INCOMING_CALL_INTENT = "android.SipDemo.INCOMING_CALL";

    public  void initSip(Context context)
    {
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(context);
        }
        this.context = context;
    }

    public void createSipProfile(String username,String password, String domain)
    {
        SipProfile.Builder builder = null;
        try {

            builder = new SipProfile.Builder("arun", "posfone.onsip.com");
            builder.setPassword(password);
            mSipProfile = builder.build();

            Intent intent = new Intent();
            intent.setAction(SIP_INCOMING_CALL_INTENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, Intent.FILL_IN_DATA);
            mSipManager.open(mSipProfile, pendingIntent, null);


            mSipManager.setRegistrationListener(mSipProfile.getUriString(),this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRegistering(String localProfileUri) {

    }

    @Override
    public void onRegistrationDone(String localProfileUri, long expiryTime) {

    }

    @Override
    public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {

    }

    public void makeCall()
    {
        try {
            mSipManager.makeAudioCall(mSipProfile.getUriString(), "sipAddress", new SipCallListenr(), 30);
        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    public void closeLocalProfile() {

        if (mSipManager == null) {
            return;
        }
        try {
            if (mSipProfile != null) {
                mSipManager.close(mSipProfile.getUriString());
            }
        } catch (Exception ee) {
            //Log.d("WalkieTalkieActivity/onDestroy", "Failed to close local profile.", ee);
        }
    }
}
