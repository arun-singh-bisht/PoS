package com.posfone.promote.posfone.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.posfone.promote.posfone.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IncomingCallActivity2 extends AppCompatActivity {

    String TAG = "PoSTestLogs  IncomingCallActivity";
    private int activeCallNotificationId;
    public static String INCOMING_CALL_STATE = "incoming_call_state";
    public static String INCOMING_CALL_NUMBER = "incoming_call_number";
    public static int INCOMING_CALL_STATE_RINGING = 1001;
    public static int INCOMING_CALL_STATE_MISSED = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_incoming_call2);

        try {


            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);


            setContentView(R.layout.activity_incoming_call2);

 /*
            String number = getIntent().getStringExtra(
                    TelephonyManager.EXTRA_INCOMING_NUMBER);
            TextView text = (TextView) findViewById(R.id.text);
            text.setText("Incoming call from " + number);*/
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent");
        int incomingCallState = intent.getIntExtra(INCOMING_CALL_STATE, 0);
        if (incomingCallState == INCOMING_CALL_STATE_RINGING) {
            Log.d(TAG, "handleIntent TelephonyManager.CALL_STATE_RINGING ");
            //Incoming Call Ringing
            String callerNumber = intent.getStringExtra(INCOMING_CALL_NUMBER);
            //txt_name.setText("N/A");
            //txt_number.setText(callerNumber);
        } else if (incomingCallState == INCOMING_CALL_STATE_MISSED) {
            //Incoming Call stopped from caller side.
            Log.d(TAG, "handleIntent TelephonyManager.CALL_STATE_IDLE ");
            finish();
        }
    }

}
