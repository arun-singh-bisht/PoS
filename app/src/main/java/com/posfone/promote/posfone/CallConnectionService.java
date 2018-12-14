package com.posfone.promote.posfone;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Chronometer;

import com.posfone.promote.posfone.Utils.TwilioTokenManager;
import com.posfone.promote.posfone.database.DatabaseHelper;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.Voice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.posfone.promote.posfone.PoSFoneApplicationClass.CHANNEL_1_ID;

public class CallConnectionService extends Service {

    private static String TAG = "PoSTestLogs CallConnectionService";
    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;
    private Call.Listener callListener = callListener();
    private Call activeCall;
    private NotificationManagerCompat notificationManager;
    private Messenger messageHandler;
    private boolean isCallActive;
    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
        mChronometer = new Chronometer(getApplicationContext());
        notificationManager = NotificationManagerCompat.from(this);
        mDatabaseHelper = new DatabaseHelper(this,"people_table");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand");
        Bundle extras = intent.getExtras();
        messageHandler = (Messenger) extras.get("MESSENGER");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        mChronometer.stop();
    }

    Chronometer getChronometer()
    {
        return mChronometer;
    }

    public void answerCall(CallInvite callInvite)
    {
        Log.d(TAG,"answerCall");
        callInvite.accept(this, callListener);

        //Add Record in Local db for Call History
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        String time=dateFormat.format(Calendar.getInstance().getTime());
        AddData(callInvite.getFrom(),callInvite.getFrom(),time);
    }

    public void disconnectCall()
    {
        Log.d(TAG,"disconnectCall");
        if(activeCall != null)
            activeCall.disconnect();
        dismissOngoingCallNotification();
    }

    public Call getActiveCall()
    {
        return activeCall;
    }

    public void makeCall(String to_number)
    {
        Log.d(TAG,"makeCall:"+to_number);
        String accessToken = new TwilioTokenManager(getApplicationContext()).getToken();
        HashMap<String, String> twiMLParams = new HashMap<>();
        twiMLParams.put("to", to_number);
        activeCall = Voice.call(getApplicationContext(), accessToken, twiMLParams, callListener);
        isCallActive = true;

        //Add Record in Local db for Call History
      /*  DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        String time=dateFormat.format(Calendar.getInstance().getTime());
        AddData(to_number,to_number,time);*/
    }

    public boolean isCallActive() { return isCallActive; }

    public void dismissOngoingCallNotification() { notificationManager.cancel(1); }

    private Call.Listener callListener() {
        return new Call.Listener() {

            @Override
            public void onConnectFailure(Call call, CallException error) {
                Log.d(TAG,"onConnectFailure");
                dismissOngoingCallNotification();
                isCallActive = false;
                //Notify to Voice Activity about Error
                Message message = Message.obtain();
                message.arg1 = VoiceActivity2.CALL_CONNECTION_FAIL;
                try {
                    messageHandler.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //Stop Service
                stopSelf();
            }

            @Override
            public void onConnected( Call call) {
                Log.d(TAG,"onConnected");
                activeCall = call;
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
                createOngoingCallNotification();
                isCallActive = true;
                //Notify to Voice Activity about connection
                Message message = Message.obtain();
                message.arg1 = VoiceActivity2.CALL_CONNECTION_CONNECTED;
                try {
                    messageHandler.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(Call call, CallException error) {
                Log.d(TAG,"onDisconnected");
                dismissOngoingCallNotification();
                isCallActive = false;
                //Notify to Voice Activity about disconnection
                Message message = Message.obtain();
                message.arg1 = VoiceActivity2.CALL_CONNECTION_DISCONNECTED;
                try {
                    messageHandler.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                //Stop Service
                stopSelf();
            }
        };
    }

    public class MyBinder extends Binder {

        CallConnectionService getService() {
            Log.d(TAG,"getService");
            return CallConnectionService.this;
        }
    }

    public void createOngoingCallNotification()
    {
        Log.d(TAG,"createOngoingCallNotification");
        Intent broadcastIntent = new Intent(this,VoiceActivity2.class);
        broadcastIntent.putExtra("toastMessage","my message");
        PendingIntent actionIntent = PendingIntent.getActivity(this,
                1, broadcastIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.call_notification)
                .setContentTitle(activeCall.getFrom())
                .setContentText("Ongoing call")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(actionIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                //.addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        startForeground(1, notification);

        //notificationManager.notify(1, notification);
    }


    public void AddData(String newEntry1,String newEntry2,String newEntry3) {
        boolean insertData = mDatabaseHelper.addData(newEntry1,newEntry2,newEntry3);
        Log.d(TAG,"Call Details Saved in DB "+insertData);
    }
}
