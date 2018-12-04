package com.posfone.promote.posfone;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;

public class Thank_You extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank__you);
        button=findViewById(R.id.btn_login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Thank_You.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        Uri data = intent.getData();
        if(data!=null) {
            String code = data.toString();
            String activationCode = code.substring(code.lastIndexOf('/') + 1);
            System.out.println("------------------------------------------------------------------------------------------");
            Log.i("SignInActivity", " " + data.toString());
            accountActivation(activationCode);
        }
    }


    private void accountActivation(String activationCode) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code",activationCode);
        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.ACCOUNT_ACTIVATION, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                try {

                    String res = response.body().string();
                    System.out.println("------------------------------------------------------------------------------------------");
                    Log.i("onResponse",res);
                    final JSONObject jsonObject = new JSONObject(res);
                    final String message = jsonObject.getString("message");

                    if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {
                        System.out.println("success");

/*
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomAlertDialog.showDialogSingleButton(SignInActivity.this, message, new CustomAlertDialog.I_CustomAlertDialog() {
                                    @Override
                                    public void onPositiveClick() {
                                    }

                                    @Override
                                    public void onNegativeClick() {

                                    }
                                });
                            }
                        });*/

                    }else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView=findViewById(R.id.all_done);
                                textView.setText(message);

                            }
                        });
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
