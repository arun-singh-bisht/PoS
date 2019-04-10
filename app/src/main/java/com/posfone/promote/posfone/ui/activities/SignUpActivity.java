package com.posfone.promote.posfone.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SmsReceiver;
import com.posfone.promote.posfone.Utils.TitilliumWebEditText;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener , Callback {


    @BindView(R.id.input_email)
    TitilliumWebEditText email;
   /* @BindView(R.id.input_email_confirmation)
    TitilliumWebEditText email_confirmation;
   */ @BindView(R.id.email_error)
    TextView error;
   @BindView(R.id.input_password)
   EditText password;
   @BindView(R.id.confirm_input_password)
   EditText confirm_password;
    /*@BindView(R.id.input_username)
    TitilliumWebEditText phone;
    @BindView(R.id.number_error)
    TextView number_error;
    @BindView(R.id.verify_phone)
    Button verify;
    @BindView(R.id.country_code)
    EditText country_code;
    *//*@BindView(R.id.otp_verification_layout)
    LinearLayout otplayout;
    @BindView(R.id.my_otp)
    EditText otp;
    @BindView(R.id.verify_otp)
    Button verify_otp;
    *//*@BindView(R.id.progress_bar)
    ProgressBar loader;
*/
    private Context mContext;
    private SmsReceiver smsReceiver;
    private TextView mTvOtp;
    private final int ACTION_FOR_COUNTRY = 1001;
    private final int ACTION_FOR_STATE = 1002;
    private final int ACTION_FOR_COUNTRY_CODE = 1003;
    String selectedCountry;
    String selectedState;
    String selectedCountryFlag;
    String selectedCode;
    private String selectedCountryPhoneCode;
    public static boolean email_Status;
    public static boolean number_Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        mContext = this;
        smsReceiver = new SmsReceiver();

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    try {
                        check_email();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    try {
                        check_phone();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });*/
        /*verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number_error.setText(" Please wait while we verify OTP send to your Number");
                Animation RightSwipe = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_slide);
                otplayout.startAnimation(RightSwipe);
                otplayout.setVisibility(View.VISIBLE);
                number_error.setVisibility(View.VISIBLE);
                number_error.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
                verify.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);

                try {
                    send_otp(getApplicationContext());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/
        initViews();
    }

    /*private void send_otp(Context context) throws JSONException {

        String text=phone.getText().toString();
        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        // header.put("email", text);
        JSONObject jsonotp = new JSONObject();
        jsonotp.put("code",country_code.getText().toString());
        jsonotp.put("number",text);
        String body = "json="+jsonotp.toString();

        Call call = RESTClient.call_POST(RESTClient.SEND_OTP, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                System.out.println("---------------------fail");
               // GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                //GeneralUtil.dismissProgressDialog();
                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            String user = jsonObject.getString("message");
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    // Stuff that updates the UI
                                    number_error.setText(" Please wait while we verify OTP send to your Number");
                                    Animation RightSwipe = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_slide);
                                    otplayout.startAnimation(RightSwipe);
                                    otplayout.setVisibility(View.VISIBLE);
                                    number_error.setVisibility(View.VISIBLE);
                                    number_error.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));
                                    generate_otp();

                                }
                            });
                            System.out.println("all details  " + user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            // Stuff that updates the UI
                            otplayout.setVisibility(View.GONE);
                            loader.setVisibility(View.GONE);
                            verify.setVisibility(View.VISIBLE);
                            verify.setText("Resend");
                            number_error.setText(" Failed to Send OTP - Resend");
                            number_error.setVisibility(View.VISIBLE);
                            number_error.setTextColor(getResources().getColor(R.color.wallet_holo_blue_light));

                        }
                    });

                }
            }
        });

    }*/

    /*private void generate_otp() {

        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
                registerReceiver(smsReceiver, intentFilter);
                Log.e("testest", "onSuccess");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("testest", "onFailure" + e.toString());
            }
        });

    }*/

    private void initViews()
    {
        findViewById(R.id.input_country).setOnClickListener(this);
        //findViewById(R.id.input_state).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
        //findViewById(R.id.country_code).setOnClickListener(this);
    }

    void check_email() throws JSONException {
                    String text=email.getText().toString();
                    if(!Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                      error.setText("*Please Enter a valid Email Address");
                        error.setTextColor(getResources().getColor(R.color.error_red));
                      error.setVisibility(View.VISIBLE);
                    return;
                    }
                    else{
                        GeneralUtil.showProgressDialog(this,"Validating Email...");
                        //Header
                        HashMap<String,String> header = new HashMap<>();
                        header.put("x-api-key", ApiClient.X_API_KEY);
                       // header.put("email", text);
                        JSONObject jsonemail = new JSONObject();
                        jsonemail.put("email",text);
                        String body = "json="+jsonemail.toString();

                        Call call = RESTClient.call_POST(RESTClient.EMAIL_CHECK, header, body, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                System.out.println("---------------------fail");
                                GeneralUtil.dismissProgressDialog();
                            }

                            @Override
                            public void onResponse(Call call, Response response) {
                                 GeneralUtil.dismissProgressDialog();
                                if (response.isSuccessful()) {
                                    try {

                                        String res = response.body().string();
                                        Log.i("onResponse", res);
                                        final JSONObject jsonObject = new JSONObject(res);

                                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                                            String user = jsonObject.getString("message");
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {

                                                    // Stuff that updates the UI

                                                    error.setText(" Valid Email Address");
                                                    error.setVisibility(View.VISIBLE);
                                                    email_Status=true;
                                                    error.setTextColor(getResources().getColor(R.color.color_light_green));

                                                }
                                            });
                                            System.out.println("all details  " + user);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {

                                            // Stuff that updates the UI
                                            email_Status=false;
                                            error.setText("* Email already Exist");
                                            error.setVisibility(View.VISIBLE);
                                            error.setTextColor(getResources().getColor(R.color.error_red));

                                        }
                                    });

                                }
                            }
                        });
                    }
    }
    /*void check_phone() throws JSONException {
        String text=phone.getText().toString();
        System.out.println("length is    ---"+phone.getText().toString().length());
        if(country_code.getText().toString().length()<1){
            number_error.setText("*Invalid Country code");
            phone.setText("");
            number_error.setTextColor(getResources().getColor(R.color.error_red));
            number_error.setVisibility(View.VISIBLE);
            return;
        }
        if(phone.getText().toString().length()<10){
            number_error.setText("*Please Enter a valid Number");
            number_error.setTextColor(getResources().getColor(R.color.error_red));
            number_error.setVisibility(View.VISIBLE);
            return;
        }

        else{
            GeneralUtil.showProgressDialog(this,"Validating Number...");
            //Header
            HashMap<String,String> header = new HashMap<>();
            header.put("x-api-key", ApiClient.X_API_KEY);
            // header.put("email", text);
            JSONObject jsonemail = new JSONObject();
            jsonemail.put("username",text);
            String body = "json="+jsonemail.toString();

            Call call = RESTClient.call_POST(RESTClient.NUMBER_CHECK, header, body, new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    System.out.println("---------------------fail");
                    GeneralUtil.dismissProgressDialog();
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) {
                    GeneralUtil.dismissProgressDialog();
                    if (response.isSuccessful()) {
                        try {

                            String res = response.body().string();
                            Log.i("onResponse", res);
                            final JSONObject jsonObject = new JSONObject(res);

                            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                                String user = jsonObject.getString("message");
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        // Stuff that updates the UI
                                        number_error.setText(" Valid Email Address");
                                        number_error.setVisibility(View.GONE);
                                        verify.setVisibility(View.VISIBLE);
                                        number_Status=true;
                                       // number_error.setTextColor(getResources().getColor(R.color.color_light_green));

                                    }
                                });
                                System.out.println("all details  " + user);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI
                                number_Status=false;
                                number_error.setText("* Email already Exist");
                                number_error.setVisibility(View.VISIBLE);
                                number_error.setTextColor(getResources().getColor(R.color.error_red));

                            }
                        });

                    }
                }
            });
        }
    }*/

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_next:{
                signUp();
            }
            break;
            case R.id.input_country:{

                Intent intent = new Intent(SignUpActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE, SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY);

            }
            break;
            case R.id.input_state:{

                String input_country =  GeneralUtil.getTextFromEditText(this,R.id.input_country);
                if(input_country.equalsIgnoreCase("Please select")){
                    String message = "Please select your Country.";
                    GeneralUtil.showToast(this,message);
                    return;
                }

                Intent intent = new Intent(SignUpActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,selectedCountry);
                intent.putExtra("selectedCountryPhoneCode",selectedCountryPhoneCode);
                intent.putExtra("selectedCountryFlag",selectedCountryFlag);
                startActivityForResult(intent,ACTION_FOR_STATE);
            }break;
            /*case R.id.country_code:{

                Intent intent = new Intent(SignUpActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY_CODE);
            }
            break;*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case ACTION_FOR_COUNTRY:
                {
                    selectedCountry = data.getStringExtra("result");
                    selectedCountryFlag = data.getStringExtra("selectedCountryFlag");
                    selectedCountryPhoneCode = data.getStringExtra("selectedCountryPhoneCode");
                    EditText editText = findViewById(R.id.input_country);
                    editText.setText(selectedCountry);

                    /*selectedState = null;
                    EditText editText_state = findViewById(R.id.input_state);
                    editText_state.setText("Please select");*/

                }break;
                case ACTION_FOR_STATE:
                {
                    selectedState = data.getStringExtra("result");
                    EditText editText = findViewById(R.id.input_state);
                    editText.setText(selectedState);
                }break;
                case ACTION_FOR_COUNTRY_CODE:
                {
                    selectedCode = data.getStringExtra("selectedCountryCode");
                    EditText editText = findViewById(R.id.country_code);
                    editText.setText(selectedCode);
                }break;
            }
        }
    }


    private void signUp()
    {

        if(!validateData())
            return;

        GeneralUtil.showProgressDialog(this,"Sign Up in progress...");

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("first_name", ((EditText)findViewById(R.id.input_fname)).getText().toString());
        //jsonObject.addProperty("last_name",((EditText)findViewById(R.id.input_lname)).getText().toString());
        //jsonObject.addProperty("company_name",((EditText)findViewById(R.id.input_company_name)).getText().toString());
        //jsonObject.addProperty("address",((EditText)findViewById(R.id.input_company_address)).getText().toString());
        jsonObject.addProperty("country",((EditText)findViewById(R.id.input_country)).getText().toString());
        /*String state = ((EditText)findViewById(R.id.input_state)).getText().toString();
        if(state.equalsIgnoreCase("Please select"))
            state = "";
        jsonObject.addProperty("state",state);
        */jsonObject.addProperty("email",((EditText)findViewById(R.id.input_email)).getText().toString());
        jsonObject.addProperty("idenity",((EditText)findViewById(R.id.input_email)).getText().toString());
        jsonObject.addProperty("password",((EditText)findViewById(R.id.input_password)).getText().toString());

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.SIGN_UP, header, body, this);

    }
    private boolean validateData()
    {
        String message = null;

        String input_country =  GeneralUtil.getTextFromEditText(this,R.id.input_country);
       // String input_state =  GeneralUtil.getTextFromEditText(this,R.id.input_state);

        /*if(!GeneralUtil.validateEditText(this,R.id.input_fname))
            message = "Enter First name.";
        */ if(input_country.equalsIgnoreCase("Please select"))
            message = "Please select your Country.";
        else if(!GeneralUtil.validateEmailEditText(this,R.id.input_email))
           message = "Enter Valid email address.";
        else if(!password.getText().toString().equals(confirm_password.getText().toString()))
            message="Confirmation Password Does not Match";
       /*else if(!GeneralUtil.validateEditText(this,R.id.input_username))
           message = "Enter Username.";*/
       else if(!GeneralUtil.validatePAsswordEditText(this,R.id.input_password))
           message = "Enter valid password .";


       if(message != null)
       {
           GeneralUtil.showToast(this,message);
           return false;
       }
        return true;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        GeneralUtil.dismissProgressDialog();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        GeneralUtil.dismissProgressDialog();

            try {

                String res = response.body().string();
                Log.i("onResponse",res);
                JSONObject jsonObject = new JSONObject(res);
                String message = jsonObject.getString("message");
                if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                    //Save User ID in SP.
                    //new SharedPreferenceHandler(SignUpActivity.this).putValue(SharedPreferenceHandler.SP_KEY_USER_ID,jsonObject.getString("user_id"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //GeneralUtil.showToast(SignUpActivity.this,"SignUp Successfull.");

                            CustomAlertDialog.showDialogSingleButton(SignUpActivity.this,"An Account Activation Link Sent to your Email Address", new CustomAlertDialog.I_CustomAlertDialog() {
                                @Override
                                public void onPositiveClick() {
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    intent.putExtra("username", ((EditText) findViewById(R.id.input_email)).getText().toString());
                                    intent.putExtra("password", ((EditText) findViewById(R.id.input_password)).getText().toString());
                                    startActivity(intent);
                                    finish();
                                }
                                @Override
                                public void onNegativeClick(){

                                }
                            });
                        }
                    });
                }else
                {
                    GeneralUtil.showToast(SignUpActivity.this,message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //-----
            }
    }

    /*@Override
    public void onOTPReceived(String msg) {
        otp.setText("OTP Number : " +  msg);
    }

    @Override
    public void onOTPTimeOut() {
        otp.setText("Timeout");
    }
    */@Override
    protected void onStop() {
        super.onStop();

       // this.unregisterReceiver(smsReceiver);
    }
}
