package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;


public class ManageNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private int ACTION_FOR_COUNTRY_OUTGOING_CALL = 1001;
    private int ACTION_FOR_COUNTRY_INCOMING_CALL = 1002;
    private String redirect_from;
    private String countryCode_callReciveNumber;
    private String countryCode_callMakingNumber;
    private String numberForReceivingCall_country;
    private String numberFoMakingCall_country;
    private String inbound_number;
    private String outbound_number;
    TextView inbound_country,outbound_country;
    TextView inbound_country_number,outbound_country_number;
    @BindView(R.id.security_code)
    EditText security_code;
    @BindView(R.id.confirm_security_code)
    EditText confirm_security_code;

    @BindView(R.id.security_layout)
    LinearLayout security_layout;
    @BindView(R.id.security_label)
    TextView security_label;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_number);
        redirect_from=getIntent().getStringExtra("redirect_from");
        ButterKnife.bind(this);
        initViews();
        get_security_pin_visibility();
        getCallProfle();
    }

    private boolean get_security_pin_visibility(){

        if (redirect_from == null){
            security_layout.setVisibility(View.VISIBLE);
            security_label.setVisibility(View.VISIBLE);
            return true;
        } else {
            security_layout.setVisibility(View.GONE);
            security_label.setVisibility(View.GONE);
            return false;
        }
    }
    private void initViews()
    {
        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Manage Number");
        inbound_country=findViewById(R.id.txt_select_country_incoming_call);
        outbound_country=findViewById(R.id.txt_select_country_outgoing_call);
        inbound_country_number=findViewById(R.id.txt_incoming_number);
        outbound_country_number=findViewById(R.id.txt_outgoing_number);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setVisibility(View.GONE);
        findViewById(R.id.btn_save).setOnClickListener(this);
        inbound_country.setOnClickListener(this);
        outbound_country.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_save:{

                String txt_incoming_number =  ((EditText)findViewById(R.id.txt_incoming_number)).getText().toString();
                String txt_outgoing_number = ((EditText)findViewById(R.id.txt_outgoing_number)).getText().toString();

                String messg = null;
                if(countryCode_callReciveNumber==null)
                    messg = "Select country of your registered number on which you will receive incoming call.";
                else if(countryCode_callMakingNumber==null)
                    messg = "Select country of your registered number from which you will make outgoing call.";
                else if(!GeneralUtil.validatePhoneNumberEditText(this,R.id.txt_incoming_number))
                    messg = "Enter valid incoming number.";
                else if(!GeneralUtil.validatePhoneNumberEditText(this,R.id.txt_outgoing_number))
                    messg = "Enter valid outgoing number.";

                // code added to check first time security pin visibility
                if (get_security_pin_visibility()) {

                    if (security_code.getText().toString().equals("") || security_code.getText() == null || security_code.getText().toString().length() != 3)
                        messg = "Please Enter 3 digit security code.";
                    else if (!security_code.getText().toString().equals(confirm_security_code.getText().toString()))
                        messg = "Confirm security code does not match.";
                }
                if(messg!=null)
                {
                    GeneralUtil.showToast(ManageNumberActivity.this,messg);
                    return;
                }

                purchaseTrialPakage(countryCode_callReciveNumber,txt_incoming_number,countryCode_callMakingNumber,txt_outgoing_number);

            }
            break;
            case R.id.txt_select_country_incoming_call:{
                Intent intent = new Intent(ManageNumberActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY_INCOMING_CALL);
            }
            break;
            case R.id.txt_select_country_outgoing_call:{
                Intent intent = new Intent(ManageNumberActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY_OUTGOING_CALL);
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ManageNumberActivity.this,PreSignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            String country_name = data.getStringExtra("result");
            String selectedCountryCode = data.getStringExtra("selectedCountryCode");
            if(requestCode == ACTION_FOR_COUNTRY_INCOMING_CALL)
            {
                countryCode_callReciveNumber = selectedCountryCode;
                ((TextView)findViewById(R.id.txt_select_country_incoming_call)).setText(country_name+"");

            }else if(requestCode == ACTION_FOR_COUNTRY_OUTGOING_CALL)
            {
                countryCode_callMakingNumber = selectedCountryCode;
                ((TextView)findViewById(R.id.txt_select_country_outgoing_call)).setText(country_name+"");
            }
            Log.i("onActivityResult",selectedCountryCode);
        }
    }

    private void purchaseTrialPakage(String inCominNumberCountryCode,String inCominNumber,String ountGoingNumberCountryCode,String outGoingNumber) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        Log.e("number",inCominNumberCountryCode+" -> "+ountGoingNumberCountryCode);
        if (inCominNumberCountryCode.equals("+") && ountGoingNumberCountryCode.equals("+")){
            inCominNumberCountryCode = "+44";
            ountGoingNumberCountryCode = "+44";
        }
        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("in1c", "+"+Uri.encode( inCominNumberCountryCode));
        jsonObject.addProperty("in1",Uri.encode( inCominNumber));
        jsonObject.addProperty("out1c","+"+Uri.encode( ountGoingNumberCountryCode));
        jsonObject.addProperty("out1",Uri.encode( outGoingNumber));
        if (get_security_pin_visibility())
        jsonObject.addProperty("security_pin",security_code.getText().toString());
        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.MANAGE_NUMBER, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            final String stripeurl = jsonObject.getString("stripeurl");
                            System.out.println("--------------------"+stripeurl);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if("profile_screen".equals(redirect_from)){
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Intent intent = new Intent(ManageNumberActivity.this, WebViewActivity.class);
                                        intent.putExtra("stripeurl", stripeurl);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    //-----------
                }

            }
        });

    }




    private void getCallProfle() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        String token = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_TOKEN);
        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        header.put("token", token);
        //RequestBody

        Call call = RESTClient.call_GET(RESTClient.MANAGE_NUMBER, header,  new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @ Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        System.out.println("--------------------------------------------------------------------------------");
                        Log.i("onResponse",res);
                        final JSONObject jsonObject = new JSONObject(res);


                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONObject mypreference =  jsonObject.getJSONObject("mypreference");
                            JSONObject in1_user_number =  mypreference.getJSONObject("in1_user_number");
                            JSONObject out1_user_number =  mypreference.getJSONObject("out1_user_number");

                            numberForReceivingCall_country = in1_user_number.getString("country");
                            countryCode_callReciveNumber = in1_user_number.getString("code");
                            inbound_number = in1_user_number.getString("number");

                            numberFoMakingCall_country = out1_user_number.getString("country");
                            countryCode_callMakingNumber = out1_user_number.getString("code");
                            outbound_number = out1_user_number.getString("number");
                            Log.e("COUNTRYNAme",numberFoMakingCall_country);
                                inbound_country.setText("United Kingdom");
                                outbound_country.setText("United Kingdom");

                            if("profile_screen".equals(redirect_from)){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                            inbound_country.setText(numberForReceivingCall_country);
                                            outbound_country.setText(numberFoMakingCall_country);

                                        inbound_country_number.setText(inbound_number.replace("+", ""));
                                        outbound_country_number.setText(outbound_number.replace("+", ""));
                                    }
                                });

                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    //-----------
                }

            }
        });

    }
}
