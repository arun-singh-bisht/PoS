package com.posfone.promote.posfone;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SoundPoolManager;
import com.twilio.voice.CallInvite;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallActivity extends AppCompatActivity {

    String TAG = "IncomingCallActivity";

    private CallInvite activeCallInvite;
    private NotificationManagerCompat notificationManager;
    private int activeCallNotificationId;
    private SoundPoolManager soundPoolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);
        ButterKnife.bind(this);
        Log.d(TAG,"onCreate");
        soundPoolManager = SoundPoolManager.getInstance(this);
        notificationManager = NotificationManagerCompat.from(this);
        handleIntent( getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"onNewIntent");
        handleIntent( intent);
    }

    private void handleIntent(Intent intent)
    {

        Log.d(TAG,"handleIntent");
        activeCallInvite = intent.getParcelableExtra(VoiceActivity.INCOMING_CALL_INVITE);
        if (activeCallInvite != null && (activeCallInvite.getState() == CallInvite.State.PENDING)) {
            //Incoming call pending...
            Log.d(TAG,"CallInvite.State.PENDING");
            soundPoolManager.playRinging();
            activeCallNotificationId = intent.getIntExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, 0);

        } else if (activeCallInvite != null && (activeCallInvite.getState() == CallInvite.State.CANCELED)) {
            //Incoming call Canceled by caller...
            Log.d(TAG,"CallInvite.State.CANCELED");
            /*
             * CallInvite.State.CANCELED will be sent by Twilio in following cases
             * A --calls-- B [ B will receive CallInvite.State.PENDING]
             * case 1: A disconnect call .[ B will receive CallInvite.State.CANCELED ]
             * case 2: B reject call. [ B will receive CallInvite.State.CANCELED ] (bugs in twilio API, confiremd by Twilio )
             * case 3: B accept call. [ B will receive CallInvite.State.CANCELED ] (bugs in twilio API, confiremd by Twilio )
             * */


                //case 1,2,3 is handled . if B accepted the call , do not close voice activity
            stopSignaling();
            finish();

        }
    }

    @OnClick(R.id.fab_decline)
    void declineCall()
    {
        stopSignaling();
        if (activeCallInvite != null) {
            activeCallInvite.reject(IncomingCallActivity.this);
        }
        finish();
    }

    @OnClick(R.id.fab_accept)
    void acceptCall()
    {
        stopSignaling();
        //send accept status to Voice Activity
        Intent intent = new Intent(this, VoiceActivity.class);
        intent.setAction(VoiceActivity.ACTION_INCOMING_CALL);
        intent.putExtra(VoiceActivity.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId);
        intent.putExtra(VoiceActivity.INCOMING_CALL_INVITE, activeCallInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //Kill this activity
        finish();

    }

    private void stopSignaling()
    {
        soundPoolManager.stopRinging();
        notificationManager.cancel(activeCallNotificationId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSignaling();
    }
}
