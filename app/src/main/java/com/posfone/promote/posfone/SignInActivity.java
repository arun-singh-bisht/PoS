package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import dmax.dialog.SpotsDialog;
import okhttp3.Call;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        intiView();
    }

    private void intiView()
    {
        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");

        findViewById(R.id.txt_title).setVisibility(View.GONE);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        if(username!=null) {
            ((EditText) findViewById(R.id.input_name)).setText(username);
            ((EditText) findViewById(R.id.input_password)).setText(password);
            findViewById(R.id.txt_account_activation_messg).setVisibility(View.VISIBLE);
        }

    }
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
            case R.id.btn_login:{

                login();

            }
            break;
        }
    }


    private void login()
    {
        //Get Values
        EditText input_name =  findViewById(R.id.input_name);
        EditText input_password =  findViewById(R.id.input_password);
        String username = input_name.getText().toString();
        String password = input_password.getText().toString();

        //Validate Values
        String message = null;
        if(!GeneralUtil.validateEditText(this,R.id.input_name))
            message = "Enter Username.";
        else if(!GeneralUtil.validatePAsswordEditText(this,R.id.input_password))
            message = "Enter valid password.";

        if(message !=null) {
            GeneralUtil.showToast(SignInActivity.this, message);
            return;
        }

        GeneralUtil.showProgressDialog(this,"Please wait");

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("password",password);
        String body = "json="+jsonObject.toString();

        //API Request
        okhttp3.Call call = RESTClient.call_POST(RESTClient.LOGIN, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

                Log.i("onFailure","onFailure");
                e.printStackTrace();
                GeneralUtil.dismissProgressDialog();

            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                Log.i("onResponse","onResponse");
                GeneralUtil.dismissProgressDialog();

                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);
                        String message = jsonObject.getString("message");

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            //Save User ID in SP.
                            final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(SignInActivity.this);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_USER_ID,jsonObject.getString("user_id"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_USER_NAME,jsonObject.getString("first_name")+" "+jsonObject.getString("last_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_2,jsonObject.getString("step2"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_3,jsonObject.getString("step3"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_4,jsonObject.getString("step4"));


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //Call Device Registration API
                                    deviceRegistration();
                                }
                            });
                        }else
                        {
                            GeneralUtil.showToast(SignInActivity.this,message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }


            }
        });

    }

    private void deviceRegistration() {

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
        jsonObject.addProperty("userid",userID);
        jsonObject.addProperty("fcm_id","dummy_fcm_xxxx");
        jsonObject.addProperty("os","android");
        jsonObject.addProperty("device_id",GeneralUtil.getImei(SignInActivity.this));

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.DEVICE_REGISTRATION, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        final JSONObject jsonObject = new JSONObject(res);
                        String message = jsonObject.getString("message");

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(SignInActivity.this);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_TOKEN,jsonObject.getString("token"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    String step2 = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_2);
                                    String step3 = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_3);
                                    String step4 = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_4);

                                    Intent intent = null;
                                    if(step2.equalsIgnoreCase("0") || step3.equalsIgnoreCase("0"))
                                    {
                                        //Open ChoosePlanActivity screen
                                        intent = new Intent(SignInActivity.this,ChoosePlanActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else if(step4.equalsIgnoreCase("0"))
                                    {
                                        //
                                        //Open Manage Number Screen
                                        intent = new Intent(SignInActivity.this,ManageNumberActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        //Open Home Screen
                                        intent = new Intent(SignInActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                        }else
                        {
                            GeneralUtil.showToast(SignInActivity.this,message);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }

            }
        });

    }
}
