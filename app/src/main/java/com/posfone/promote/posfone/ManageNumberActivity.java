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

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;


public class ManageNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private int ACTION_FOR_COUNTRY_OUTGOING_CALL = 1001;
    private int ACTION_FOR_COUNTRY_INCOMING_CALL = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_number);

        initViews();
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

                String countryCodeForIncomingCall =  ((TextView)findViewById(R.id.txt_select_country_incoming_call)).getText().toString();
                String countryCodeForOutgoingCall = ((TextView)findViewById(R.id.txt_select_country_outgoing_call)).getText().toString();
                String txt_incoming_number =  ((EditText)findViewById(R.id.txt_incoming_number)).getText().toString();
                String txt_outgoing_number = ((EditText)findViewById(R.id.txt_outgoing_number)).getText().toString();

                String messg = null;
                if(countryCodeForIncomingCall.equalsIgnoreCase("Select Country") || countryCodeForOutgoingCall.equalsIgnoreCase("Select Country") )
                    messg = "Select country code.";
                else if(!GeneralUtil.validatePhoneNumberEditText(this,R.id.txt_incoming_number))
                    messg = "Enter valid incoming number.";
                else if(!GeneralUtil.validatePhoneNumberEditText(this,R.id.txt_outgoing_number))
                    messg = "Enter valid outgoing number.";


                if(messg!=null)
                {
                    GeneralUtil.showToast(ManageNumberActivity.this,messg);
                    return;
                }

                purchaseTrialPakage(countryCodeForIncomingCall,txt_incoming_number,countryCodeForOutgoingCall,txt_outgoing_number);


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
        Intent intent = new Intent(ManageNumberActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            String country_name = data.getStringExtra("result");
            String selectedCountryPhoneCode = data.getStringExtra("selectedCountryPhoneCode");
            if(requestCode == ACTION_FOR_COUNTRY_INCOMING_CALL)
            {
                ((TextView)findViewById(R.id.txt_select_country_incoming_call)).setText(country_name+"("+selectedCountryPhoneCode+")");
            }else if(requestCode == ACTION_FOR_COUNTRY_OUTGOING_CALL)
            {
                ((TextView)findViewById(R.id.txt_select_country_outgoing_call)).setText(country_name+"("+selectedCountryPhoneCode+")");
            }
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
        jsonObject.addProperty("in1c",inCominNumberCountryCode);
        jsonObject.addProperty("in1",inCominNumber);
        jsonObject.addProperty("out1c",ountGoingNumberCountryCode);
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


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ManageNumberActivity.this,MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
}
