package com.posfone.promote.posfone;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        intiView();
    }

    private void intiView()
    {

        findViewById(R.id.txt_title).setVisibility(View.GONE);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
        }
    }


    @OnClick(R.id.btn_reset)
    public void login()
    {

        if(!GeneralUtil.validateEmailEditText(ForgotPasswordActivity.this,R.id.input_email))
        {
            GeneralUtil.showToast(ForgotPasswordActivity.this,"Enter a valid email address.");
            return;
        }

        //Get Values
        EditText input_email =  findViewById(R.id.input_email);
        String email = input_email.getText().toString();

        GeneralUtil.showProgressDialog(this,"Please wait");

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);

        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        String body = "json="+jsonObject.toString();

        //API Request
        Call call = RESTClient.call_POST(RESTClient.FORGOT_PASSWORD, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.i("onFailure","onFailure");
                e.printStackTrace();
                GeneralUtil.dismissProgressDialog();

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                Log.i("onResponse","onResponse");
                GeneralUtil.dismissProgressDialog();

                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);
                        String message = jsonObject.getString("message");

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    setResult(RESULT_OK);
                                    finish();
                                }
                            });
                        }else
                        {
                            GeneralUtil.showToast(ForgotPasswordActivity.this,message);
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
