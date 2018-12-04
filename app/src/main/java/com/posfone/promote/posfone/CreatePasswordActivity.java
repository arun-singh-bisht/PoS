package com.posfone.promote.posfone;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class CreatePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.input_password)
    EditText input_password;
    @BindView(R.id.input_confirm_password)
    EditText input_confirm_password;

    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        ButterKnife.bind(this);

        intiView();
    }

    private void intiView()
    {

        findViewById(R.id.txt_title).setVisibility(View.GONE);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        code = data.toString();
        code = code.substring(code.lastIndexOf('/')+1);
        Log.i("CreatePasswordActivity",action+" "+data.toString());

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


    @OnClick(R.id.btn_update)
    public void updatePassword()
    {

        if(!GeneralUtil.validatePAsswordEditText(this,R.id.input_password))
        {
            GeneralUtil.showToast(CreatePasswordActivity.this,"Enter a valid password.");
            return;
        }
        else if(!input_password.getText().toString().equalsIgnoreCase(input_confirm_password.getText().toString()))
        {
            GeneralUtil.showToast(CreatePasswordActivity.this,"Confirm Password do not match.");
            return;
        }


        GeneralUtil.showProgressDialog(this,"Please wait");

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);

        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("password", input_password.getText().toString());

        String body = "json="+jsonObject.toString();

        //API Request
        Call call = RESTClient.call_POST(RESTClient.RESET_PASSWORD, header, body, new okhttp3.Callback() {
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

                                    CustomAlertDialog.showDialogSingleButton(CreatePasswordActivity.this, "Your password has been changed now.", new CustomAlertDialog.I_CustomAlertDialog() {
                                        @Override
                                        public void onPositiveClick() {
                                            //Redirect user to PreSignInActivity
                                            Intent intent = new Intent(CreatePasswordActivity.this,PreSignInActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
                                }
                            });
                        }else
                        {
                            GeneralUtil.showToast(CreatePasswordActivity.this,message);
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
