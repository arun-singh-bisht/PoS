package com.posfone.promote.posfone.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.SoundPoolManager;
import com.posfone.promote.posfone.background.services.CallConnectionService;
import com.posfone.promote.posfone.ui.fragments.ContactFragment;
import com.twilio.voice.Call;
import com.twilio.voice.CallInvite;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VoiceActivity3 extends AppCompatActivity  {

    private static final String TAG = "VoiceActivity2";
    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_OUTGOING_CALL = "ACTION_OUTGOING_CALL";

    public static final int CALL_CONNECTION_FAIL = 5001;
    public  static final int CALL_CONNECTION_CONNECTED = 5002;
    public  static final int CALL_CONNECTION_DISCONNECTED = 5003;

    private AudioManager audioManager;
    private Call activeCall;
    private CallConnectionService callConnectionService;
    private Handler messageHandler = new MessageHandler();;

    private int savedAudioMode = AudioManager.MODE_INVALID;

    @BindView(R.id.txt_dialing)
    TextView txt_dialing;
    @BindView(R.id.txt_name)
    TextView txt_name;
    @BindView(R.id.txt_number)
    TextView txt_number;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.call_action_fab)
    FloatingActionButton callActionFab;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.payemnt)
    Button payment;
    @BindView(R.id.buttonDial)
    FloatingActionButton dial;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        ButterKnife.bind(this);
        //Keyboard Disabled Here
        input.setShowSoftInputOnFocus(false);

        initViews();

    }

    private void initViews()
    {

        dial.setVisibility(View.INVISIBLE);


        Animation anim = new AlphaAnimation(0.5f, 0.7f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txt_dialing.setText("Connecting");
        txt_dialing.startAnimation(anim);

        callActionFab.setOnClickListener(callActionFabClickListener());

        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        //Start Background bound service to handle call connection
        Intent callConnectionServiceIntent = new Intent(this, CallConnectionService.class);
        callConnectionServiceIntent.putExtra("MESSENGER", new Messenger(messageHandler));
        startService(callConnectionServiceIntent);
        bindService(callConnectionServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
            //mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            CallConnectionService.MyBinder myBinder = (CallConnectionService.MyBinder) service;
            callConnectionService = myBinder.getService();

            //handle incoming intent
            handleIncomingCallIntent(getIntent());
        }
    };


    private void handleIncomingCallIntent(Intent intent) {
        Log.d(TAG,"handleIncomingCallIntent");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_INCOMING_CALL)){
                Log.d(TAG,"handleIncomingCallIntent ACTION_INCOMING_CALL");
                CallInvite activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);

                txt_name.setText(activeCallInvite.getFrom());
                txt_number.setText(activeCallInvite.getFrom());

               if(callConnectionService!=null)
                   callConnectionService.answerCall(activeCallInvite);

            }else if(intent.getAction().equals(ACTION_OUTGOING_CALL))
            {

                Log.d(TAG,"handleIncomingCallIntent ACTION_OUTGOING_CALL");
                String to_number = intent.getStringExtra("to_number");
                txt_name.setText(to_number);
                txt_number.setText(to_number);
                if(callConnectionService!=null)
                    callConnectionService.makeCall(to_number);

            }
        }else
        {
            Log.d(TAG,"handleIncomingCallIntent null");

            if(callConnectionService!=null && callConnectionService.isCallActive()) {
                Log.d(TAG,"handleIncomingCallIntent callConnectionService.isCallActive() == true");
                activeCall = callConnectionService.getActiveCall();
                txt_dialing.setText("Connected to");
                txt_name.setText(activeCall.getFrom());
                setCallUI();
            }
        }
    }

    @Override
    public void onBackPressed(){
         if(findViewById(R.id.mylayout).getVisibility()==View.VISIBLE)
         {
             System.out.println("visible-------------");
         findViewById(R.id.calling_detail).setVisibility(View.VISIBLE);
         findViewById(R.id.calling_image).setVisibility(View.VISIBLE);
         findViewById(R.id.call_action_fab).setVisibility(View.VISIBLE);
         findViewById(R.id.mylayout).setVisibility(View.INVISIBLE);
         }
         else{
             finish();
         }
    }


    private View.OnClickListener callActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callConnectionService.isCallActive()) {
                    //Disconnect call
                    SoundPoolManager soundPoolManager = SoundPoolManager.getInstance(getApplicationContext());
                    soundPoolManager.playDisconnect();
                    callConnectionService.disconnectCall();

                }
            }
        };
    }


    public class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            Log.d(TAG,"handleMessage");
            int state = message.arg1;
            switch (state) {
                case CALL_CONNECTION_FAIL:
                    Log.d(TAG,"handleMessage CALL_CONNECTION_FAIL");
                    Log.d(TAG, "Connect failure");
                    setAudioFocus(false);
                    finish();
                    break;
                case CALL_CONNECTION_CONNECTED:
                    Log.d(TAG,"handleMessage CALL_CONNECTION_CONNECTED");
                    setAudioFocus(true);

                    input.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            String s=String.valueOf(charSequence);
                            System.out.println("string to send "+s.substring(i,s.length()));
                            activeCall.sendDigits(s.substring(i,s.length()));

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                    payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activeCall.sendDigits("729#");
                        }
                    });
                    setCallUI();

                    break;
                case CALL_CONNECTION_DISCONNECTED:
                    Log.d(TAG,"handleMessage CALL_CONNECTION_DISCONNECTED");
                    setAudioFocus(false);
                    finish();
                    break;
            }
        }
    }


    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {

        txt_dialing.clearAnimation();
        txt_dialing.setAnimation(null);
        txt_dialing.setText("Connected");
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(callConnectionService.getChronometer().getBase());
        chronometer.start();
    }

    //-----------------------------------------UI Things---------------------------------------------


//-------------------------------------Operation for Dialer Input---------------------------
@OnClick(R.id.buttonOne)
public void one() {
    onButtonClick(input ,"1" );
}
    @OnClick(R.id.buttonTwo)
    public void two() {
        onButtonClick(input ,"2" );
    }
    @OnClick(R.id.buttonThree)
    public void three() {
        onButtonClick(input ,"3" );
    }
    @OnClick(R.id.buttonFour)
    public void four() {
        onButtonClick(input ,"4" );
    }
    @OnClick(R.id.buttonFive)
    public void five() {
        onButtonClick(input ,"5" );
    }
    @OnClick(R.id.buttonSix)
    public void six() {
        onButtonClick(input ,"6" );
    }
    @OnClick(R.id.buttonSeven)
    public void seven() {
        onButtonClick(input ,"7" );
    }
    @OnClick(R.id.buttonHash)
    public void hash() {
        onButtonClick(input ,"#" );
    }
    @OnClick(R.id.buttonEight)
    public void eight() {
        onButtonClick(input ,"8" );
    }
    @OnClick(R.id.buttonNine)
    public void nine() {
        onButtonClick(input ,"9" );
    }
    @OnClick(R.id.buttonZero)
    public void zero() {
        onButtonClick(input ,"0" );
    }
    @OnClick(R.id.buttonStar)
    public void star() {
        onButtonClick(input ,"*" );
    }


    public void onButtonClick( EditText inputNumber, String number) {
        inputNumber.append(number);
    }

    public String getName(String number) {
        String name="No Name";
        for (int i=0;i<ContactFragment.searchContacts.size();i++){
          // System.out.println(ContactFragment.searchContacts.get(i).getContactNumber()+"----"+ContactFragment.searchContacts.get(i).getContactName());
            if(PhoneNumberUtils.compare(number, ContactFragment.searchContacts.get(i).getContactNumber())){
                System.out.println("found Match --------"+ ContactFragment.searchContacts.get(i).getContactName());
                name=ContactFragment.searchContacts.get(i).getContactName();
                return name;
            }

        }
        return name;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.icon_speaker)
    public void speaker() {
        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isSpeakerphoneOn()) {
            //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setSpeakerphoneOn(false);
            findViewById(R.id.icon_speaker).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            findViewById(R.id.icon_speaker).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
            audioManager.setSpeakerphoneOn(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.icon_mic)
    public void mic(){

        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isMicrophoneMute()) {
            //audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMicrophoneMute(false);
            findViewById(R.id.icon_mic).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } else {
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            findViewById(R.id.icon_mic).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));
            audioManager.setMicrophoneMute(true);
        }

    }
    @OnClick(R.id.icon_dialpad)
    public void dialpad(){
        findViewById(R.id.mylayout).setVisibility(View.VISIBLE);
        findViewById(R.id.calling_detail).setVisibility(View.INVISIBLE);
        findViewById(R.id.calling_image).setVisibility(View.INVISIBLE);
        findViewById(R.id.call_action_fab).setVisibility(View.INVISIBLE);
        findViewById(R.id.payemnt).setVisibility(View.VISIBLE);
    }


    private void setAudioFocus(boolean setFocus) {
        if (audioManager != null) {
            if (setFocus) {
                savedAudioMode = audioManager.getMode();
                // Request audio focus before making any device switch.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    AudioAttributes playbackAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build();
                    AudioFocusRequest focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                            .setAudioAttributes(playbackAttributes)
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(new AudioManager.OnAudioFocusChangeListener() {
                                @Override
                                public void onAudioFocusChange(int i) {
                                }
                            })
                            .build();
                    audioManager.requestAudioFocus(focusRequest);
                } else {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }
                /*
                 * Start by setting MODE_IN_COMMUNICATION as default audio mode. It is
                 * required to be in this mode when playout and/or recording starts for
                 * best possible VoIP performance. Some devices have difficulties with speaker mode
                 * if this is not set.
                 */
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(savedAudioMode);
                audioManager.abandonAudioFocus(null);
            }
        }
    }
}
