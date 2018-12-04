package com.posfone.promote.posfone;

import android.app.Notification;
/*
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Number;*/
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.Utils.SoundPoolManager;
import com.posfone.promote.posfone.database.DatabaseHelper;
import com.posfone.promote.posfone.fragment.ContactFragment;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.Voice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.posfone.promote.posfone.PoSFoneApplicationClass.CHANNEL_1_ID;
import java.util.Calendar;

public class VoiceActivity extends AppCompatActivity  {

    private static final String TAG = "VoiceActivity";
    private static String identity = "16617480240";
    private static String to_number = "";
    DatabaseHelper mDatabaseHelper;
    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "http://protechgenie.in/demo/accessToken.php";
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.payemnt)
    Button payment;
    @BindView(R.id.buttonDial)
    FloatingActionButton dial;


    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    private static final int SNACKBAR_DURATION = 4000;

    private String accessToken;

    private AudioManager audioManager;
    private int savedAudioMode = AudioManager.MODE_INVALID;

    private boolean isReceiverRegistered = false;

    // Empty HashMap, never populated for the Quickstart
    HashMap<String, String> twiMLParams = new HashMap<>();

    private FloatingActionButton callActionFab;
    private ImageView hangupAction;
    private ImageView muteAction;
    private TextView txt_dialing;
    public Animation animation;
    private Chronometer chronometer;
    private SoundPoolManager soundPoolManager;

    public static final String INCOMING_CALL_INVITE = "INCOMING_CALL_INVITE";
    public static final String INCOMING_CALL_NOTIFICATION_ID = "1";
    public static final String ACTION_INCOMING_CALL = "ACTION_INCOMING_CALL";
    public static final String ACTION_FCM_TOKEN = "ACTION_FCM_TOKEN";

    Button buttonplus;
    private NotificationManagerCompat notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private Call activeCall;
    private int activeCallNotificationId;
    Date currentTime = Calendar.getInstance().getTime();

    Call.Listener callListener = callListener();
    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private boolean isCallActive = false;
    private String to_name;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_voice);
        buttonplus=findViewById(R.id.buttonZero);

        buttonplus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onButtonClick(input ,"+" );
                return true;
            }
        });

        ButterKnife.bind(this);
        System.out.println("zrunning on create------");
        //Keyboard Disabled Here
        input.setShowSoftInputOnFocus(false);
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notificationManager = NotificationManagerCompat.from(this);
        mDatabaseHelper = new DatabaseHelper(this,"people_table");
        callnotification();
        identity =  getIntent().getStringExtra("from_number");
        to_number = getIntent().getStringExtra("to_number");
        to_name = getIntent().getStringExtra("to_name");
        identity = "16617480241";
        //get Current preferred selection number From Settings
        SharedPreferenceHandler sharedPreferenceHandler=new SharedPreferenceHandler(this);
       String caller_id= sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_CALLER_ID);
       String number=sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER);

       /*if(!"Pay729 Number".equals(caller_id))
        identity="91"+number;*/
       if(to_number!=null)
        to_number = to_number.replace(" ","");
       //to_number = "918209874738";
        initViews();
        super.onCreate(savedInstanceState);
        // Call REceive
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();


    }

    private void callnotification() {

        Intent broadcastIntent = new Intent(this,VoiceActivity.class);
        broadcastIntent.putExtra("toastMessage","my message");
        PendingIntent actionIntent = PendingIntent.getActivity(this,
                1, broadcastIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
                .setSmallIcon(R.drawable.call_notification)
                .setContentTitle("Call In Progress")
                .setContentText("Tap to Open")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(actionIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                //.addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        notificationManager.notify(1, notification);

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
             Intent intent=new Intent(this,MainActivity.class);
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(intent);
             /*soundPoolManager.playDisconnect();
             disconnect();
             resetUI();*/
            // Toast.makeText(this,"Call In Progress !!",Toast.LENGTH_SHORT).show();
        //super.onBackPressed();
         }
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }


    private void initViews()
    {
         String name=getName(to_number);
        ((TextView)findViewById(R.id.txt_name)).setText(name);
        ((TextView)findViewById(R.id.txt_number)).setText(to_number);
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        String time=dateFormat.format(currentTime);
        dial.setVisibility(View.INVISIBLE);
            AddData(name,to_number,time);

        txt_dialing = findViewById(R.id.txt_dialing);
        Animation anim = new AlphaAnimation(0.5f, 0.7f);
        anim.setDuration(500); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txt_dialing.startAnimation(anim);
        callActionFab = findViewById(R.id.call_action_fab);
        //muteAction = findViewById(R.id.icon_speaker);
        chronometer = findViewById(R.id.chronometer);

        callActionFab.setOnClickListener(callActionFabClickListener());
        //hangupAction.setOnClickListener(hangupActionFabClickListener());
        soundPoolManager = SoundPoolManager.getInstance(this);
        audioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        retrieveAccessToken();

    }

    




    private View.OnClickListener callActionFabClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCallActive) {
                    //Disconnect call
                    soundPoolManager.playDisconnect();
                    disconnect();
                    resetUI();
                }else
                {
                    //Connect Call
                    makeCall();
                }
            }
        };
    }


    private void makeCall()
    {
        twiMLParams.put("to", to_number);
        activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);
        isCallActive = true;
    }



    private Call.Listener callListener() {
        return new Call.Listener() {

            @Override
            public void onConnectFailure(Call call, CallException error) {
                setAudioFocus(false);
                Log.d(TAG, "Connect failure");
                String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                Log.e(TAG, message);
                showToast(message);
                resetUI();
            }

            @Override
            public void onConnected( Call call) {
                setAudioFocus(true);
                Log.d(TAG, "Connected");
                activeCall = call;
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
            }

            @Override
            public void onDisconnected(Call call, CallException error) {
                setAudioFocus(false);
                Log.d(TAG, "Disconnected");
                notificationManager.cancel(1);
                if (error != null) {
                    String message = String.format("Call Error: %d, %s", error.getErrorCode(), error.getMessage());
                    Log.e(TAG, message);
                    showToast(message);
                }
                resetUI();
            }
        };
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




    /*
     * Reset UI elements
     */
    private void resetUI() {
        isCallActive = false;
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
        finish();
    }


    /*
     * The UI state when there is an active call
     */
    private void setCallUI() {
        txt_dialing.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }



    /*
     * Disconnect from Call
     */
    private void disconnect() {
        notificationManager.cancel(1);
        finish();
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }


    @Override
    public void onDestroy() {
        soundPoolManager.release();
        super.onDestroy();
    }



    /*
     * Get an access token from your Twilio access token server
     */
    private void retrieveAccessToken() {
        Ion.with(this).load(TWILIO_ACCESS_TOKEN_SERVER_URL + "?identity=" + identity).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null) {
                    Log.d(TAG, "Access token: " + accessToken);
                    VoiceActivity.this.accessToken = accessToken;
                    //registerForCallInvites();
                    makeCall();
                } else {
                    showToast("Error retrieving access token. Unable to make calls");
                }
            }
        });
    }

    private void showToast(String msg)
    {
        Toast.makeText(VoiceActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
    public void AddData(String newEntry1,String newEntry2,String newEntry3) {

        boolean insertData = mDatabaseHelper.addData(newEntry1,newEntry2,newEntry3);

        if (insertData) {

            showToast("Data Successfully Inserted!");

        } else {

            showToast("Something went wrong");

        }

    }

// Reciving call here----------------------------------------
    private class VoiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ACTION_INCOMING_CALL)) {
                /*
                 * Handle the incoming call invite
                 */
                handleIncomingCallIntent(intent);
            }
        }
    }

    private void handleIncomingCallIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_INCOMING_CALL)){
                activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);
                if (activeCallInvite != null && (activeCallInvite.getState() == CallInvite.State.PENDING)) {
                    soundPoolManager.playRinging();
                    alertDialog = createIncomingCallDialog(VoiceActivity.this,
                            activeCallInvite,
                            answerCallClickListener(),
                            cancelCallClickListener());
                    alertDialog.show();
                    activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);
                } else {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        soundPoolManager.stopRinging();
                        alertDialog.cancel();
                    }
                }
            } else if (intent.getAction().equals(ACTION_FCM_TOKEN)) {
                retrieveAccessToken();
            }
        }
    }

    //  INcoming Call Dialog
    public static AlertDialog createIncomingCallDialog(
            Context context,
            CallInvite callInvite,
            DialogInterface.OnClickListener answerCallClickListener,
            DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.call_icon);
        alertDialogBuilder.setTitle("Incoming Call");
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");
        return alertDialogBuilder.create();
    }

    // Dialog Listenerss for CAll



    private DialogInterface.OnClickListener cancelCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                soundPoolManager.stopRinging();
                if (activeCallInvite != null) {
                    activeCallInvite.reject(VoiceActivity.this);
                    notificationManager.cancel(activeCallNotificationId);
                }
                alertDialog.dismiss();
            }
        };
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                soundPoolManager.stopRinging();
                answer();
                setCallUI();
                alertDialog.dismiss();
            }
        };
    }

    /*
     * Accept an incoming Call
     */
    private void answer() {
        activeCallInvite.accept(this, callListener);
        notificationManager.cancel(activeCallNotificationId);
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_INCOMING_CALL);
            intentFilter.addAction(ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

//--------------------------

/*
    private void handleIncomingCallIntent(Intent intent) {

        if (intent != null && intent.getAction() != null) {

            if (intent.getAction().equals(ACTION_INCOMING_CALL)) {

                activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE);

                if (activeCallInvite != null && (activeCallInvite.getState() == CallInvite.State.PENDING)) {

                    soundPoolManager.playRinging();

                    alertDialog = createIncomingCallDialog(VoiceActivity.this,

                            activeCallInvite,

                            answerCallClickListener(),

                            cancelCallClickListener());

                    alertDialog.show();

                    activeCallNotificationId = intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0);

                } else {

                    if (alertDialog != null && alertDialog.isShowing()) {

                        soundPoolManager.stopRinging();

                        alertDialog.cancel();

                    }

                }

            } else if (intent.getAction().equals(ACTION_FCM_TOKEN)) {

                retrieveAccessToken();

            }

        }

    }
    private DialogInterface.OnClickListener answerCallClickListener() {

        return new DialogInterface.OnClickListener() {



            @Override

            public void onClick(DialogInterface dialog, int which) {

                soundPoolManager.stopRinging();

                answer();

                setCallUI();

                alertDialog.dismiss();

            }

        };

    }



    private DialogInterface.OnClickListener callClickListener() {

        return new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

                // Place a call

                EditText contact = (EditText) ((AlertDialog) dialog).findViewById(R.id.contact);

                twiMLParams.put("to", contact.getText().toString());

                activeCall = Voice.call(VoiceActivity.this, accessToken, twiMLParams, callListener);

                setCallUI();

                alertDialog.dismiss();

            }

        };

    }



    private DialogInterface.OnClickListener cancelCallClickListener() {

        return new DialogInterface.OnClickListener() {



            @Override

            public void onClick(DialogInterface dialogInterface, int i) {

                soundPoolManager.stopRinging();

                if (activeCallInvite != null) {

                    activeCallInvite.reject(VoiceActivity.this);

                    notificationManager.cancel(activeCallNotificationId);

                }

                alertDialog.dismiss();

            }

        };

    }



    public static AlertDialog createIncomingCallDialog(

            Context context,

            CallInvite callInvite,

            DialogInterface.OnClickListener answerCallClickListener,

            DialogInterface.OnClickListener cancelClickListener) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);

        alertDialogBuilder.setTitle("Incoming Call");

        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);

        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);

        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling.");

        return alertDialogBuilder.create();

    }





  */
/*   * Register your FCM token with Twilio to receive incoming call invites

     *

     * If a valid google-services.json has not been provided or the FirebaseInstanceId has not been

     * initialized the fcmToken will be null.

     *

     * In the case where the FirebaseInstanceId has not yet been initialized the

     * VoiceFirebaseInstanceIDService.onTokenRefresh should result in a LocalBroadcast to this

     * activity which will attempt registerForCallInvites again.

     *

     *//*



    private void registerForCallInvites() {

        final String fcmToken = FirebaseInstanceId.getInstance().getToken();

        if (fcmToken != null) {

            Log.i(TAG, "Registering with FCM");

            Voice.register(this, accessToken, Voice.RegistrationChannel.FCM, fcmToken, registrationListener);

        }

    }



    private View.OnClickListener callActionFabClickListener() {

        return new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), VoiceActivity.this);

                alertDialog.show();

            }

        };

    }



    private View.OnClickListener hangupActionFabClickListener() {

        return new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                soundPoolManager.playDisconnect();

                resetUI();

                disconnect();

            }

        };

    }



    private View.OnClickListener muteActionFabClickListener() {

        return new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                mute();

            }

        };

    }
*/




/*

     * Accept an incoming Call

     *//*


    private void answer() {

        activeCallInvite.accept(this, callListener);

        notificationManager.cancel(activeCallNotificationId);

    }

*/


    /*

     * Disconnect from Call

     */



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


    /*@OnClick(R.id.buttonDelete)
    public void delete(){
        if(input.getText().toString().length()>=1) {
            String newScreen = input.getText().toString().substring(0, input.getText().toString().length() - 1);
            input.setText(newScreen);
        }
    }*/

    public void onButtonClick( EditText inputNumber, String number) {
        inputNumber.append(number);
    }

    public String getName(String number) {
        String name="No Name";
        for (int i=0;i<ContactFragment.searchContacts.size();i++){
          // System.out.println(ContactFragment.searchContacts.get(i).getContactNumber()+"----"+ContactFragment.searchContacts.get(i).getContactName());
            if(PhoneNumberUtils.compare(number, ContactFragment.searchContacts.get(i).getContactNumber())){
                System.out.println("found Match --------"+ContactFragment.searchContacts.get(i).getContactName());
                name=ContactFragment.searchContacts.get(i).getContactName();
                return name;
            }

        }
        return name;
    }
/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putString("Email", to_name);
        savedInstanceState.putString("password",to_number);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        String email_g = savedInstanceState.getString("Email");
        String password=savedInstanceState.getString("password");
        ((TextView)findViewById(R.id.txt_name)).setText(email_g);
        ((TextView)findViewById(R.id.txt_number)).setText(password);
    }
*/

}
