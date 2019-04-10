package com.posfone.promote.posfone.features.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.posfone.promote.posfone.Utils.GeneralUtil;


public class IncomingCallReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent) {

        GeneralUtil.showToast(context,"onReceive");

       // Voice.register(context,"sfdfsf",null,null);
    }
}

