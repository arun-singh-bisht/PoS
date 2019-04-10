package com.posfone.promote.posfone.features.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;

import java.util.Map;

public class VoiceFirebaseMessagingService2 extends FirebaseMessagingService {

    private static final String TAG = "VoiceFCMService2";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Received onMessageReceived()");
        Log.d(TAG, "Bundle data: " + remoteMessage.getData());
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "title: " + remoteMessage.getNotification().getTitle());
        //Log.d(TAG, "body: " + remoteMessage.getNotification().getBody());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG,"callerNumber:"+data.get("callerNumber"));
            new SharedPreferenceHandler(getApplicationContext()).putValue(SharedPreferenceHandler.SP_KEY_INCOMING_CALL_CALLER_NUMBER,data.get("callerNumber"));
        }

    }
}
