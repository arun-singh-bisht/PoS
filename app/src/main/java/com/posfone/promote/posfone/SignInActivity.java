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

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        intiView();
    }

    private void intiView()
    {
        findViewById(R.id.txt_title).setVisibility(View.GONE);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

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
        if(username.isEmpty() || password.isEmpty()) {
            GeneralUtil.showToast(SignInActivity.this,getString(R.string.invalid_credentials));
            return;
        }


        //Show loading dialog
        final AlertDialog progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait")
                .build();
        progressDialog.show();

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

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();


            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                Log.i("onResponse","onResponse");
                if(progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();


                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

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

                                    String step = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_SIGN_UP_STEP_2);

                                    Intent intent = null;
                                    if(step.equalsIgnoreCase("0"))
                                    {
                                        //Open ChoosePlanActivity screen
                                        intent = new Intent(SignInActivity.this,ChoosePlanActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {

                                            //Open package screen
                                            intent = new Intent(SignInActivity.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();


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
                    Log.i("onResponse","Not Successfull");
                }

            }
        });

    }

}
