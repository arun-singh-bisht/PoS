package com.posfone.promote.posfone.background.receivers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.posfone.promote.posfone.Utils.PhoneContactUtils;
import com.posfone.promote.posfone.background.services.FloatingViewService;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;

import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {

    private String merchantTwillioNumber = "+919887808872";

    @Override
    protected void onIncomingCallStarted(final Context ctx,final String number, Date start) {
        Log.d("CallReceiver","onIncomingCallStarted: "+number);
        if(number.equalsIgnoreCase(merchantTwillioNumber)) {

            new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {

                   //Get number from SP saved through FCM push
                   String savedCallerNumber = new SharedPreferenceHandler(ctx).getStringValue(SharedPreferenceHandler.SP_KEY_INCOMING_CALL_CALLER_NUMBER);
                   String contactName = "-";
                   if(savedCallerNumber!=null && !savedCallerNumber.isEmpty())
                     contactName = PhoneContactUtils.getContactName(ctx, savedCallerNumber);
                   else
                       savedCallerNumber = "-";

                   Intent intent = new Intent(ctx, FloatingViewService.class);
                   intent.putExtra("name",contactName);
                   intent.putExtra("number",savedCallerNumber);

                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                       ctx.startForegroundService(intent);
                   } else {
                       ctx.startService(intent);
                   }

               }
           },1000);

        }
    }

    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start) {
        Log.d("CallReceiver","onIncomingCallAnswered: "+number);
        if(number!=null && number.equalsIgnoreCase(merchantTwillioNumber))
        {
            ctx.stopService(new Intent(ctx, FloatingViewService.class));
        }
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("CallReceiver","onIncomingCallEnded: "+number);if(number!=null && number.equalsIgnoreCase(merchantTwillioNumber))
        {
            ctx.stopService(new Intent(ctx, FloatingViewService.class));
        }

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d("CallReceiver","onOutgoingCallStarted: "+number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("CallReceiver","onOutgoingCallEnded: "+number);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        Log.d("CallReceiver","onMissedCall: "+number);
        if(number!=null && number.equalsIgnoreCase(merchantTwillioNumber))
        {
            ctx.stopService(new Intent(ctx, FloatingViewService.class));
        }
    }

}
