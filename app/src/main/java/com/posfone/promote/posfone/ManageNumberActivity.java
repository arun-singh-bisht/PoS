package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;


public class ManageNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private int ACTION_FOR_COUNTRY_OUTGOING_CALL = 1001;
    private int ACTION_FOR_COUNTRY_INCOMING_CALL = 1002;

    private String countryCode_callReciveNumber;
    private String countryCode_callMakingNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_number);

        initViews();

        getCallProfle();
    }

    private void initViews()
    {
        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Manage Number");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setVisibility(View.GONE);
        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.txt_select_country_incoming_call).setOnClickListener(this);
        findViewById(R.id.txt_select_country_outgoing_call).setOnClickListener(this);

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

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("in1c","+"+inCominNumberCountryCode);
        jsonObject.addProperty("in1",inCominNumber);
        jsonObject.addProperty("out1c","+"+ountGoingNumberCountryCode);
        jsonObject.addProperty("out1",outGoingNumber);
        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.MANAGE_NUMBER, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            final String stripeurl = jsonObject.getString("stripeurl");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ManageNumberActivity.this,WebViewActivity.class);
                                    intent.putExtra("stripeurl",stripeurl);
                                    //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
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
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @ Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONObject mypreference =  jsonObject.getJSONObject("mypreference");
                            JSONObject in1_user_number =  mypreference.getJSONObject("in1_user_number");
                            JSONObject out1_user_number =  mypreference.getJSONObject("out1_user_number");

                            String numberForReceivingCall_country = in1_user_number.getString("country");
                            countryCode_callReciveNumber = "+"+in1_user_number.getString("code");

                            String numberFoMakingCall_country = out1_user_number.getString("country");
                            countryCode_callMakingNumber = "+"+out1_user_number.getString("code");

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
