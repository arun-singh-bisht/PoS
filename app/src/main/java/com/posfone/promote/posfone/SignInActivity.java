package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.model.LoginResponse;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.ApiInterface;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener, Callback<LoginResponse> {

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

                EditText input_name =  findViewById(R.id.input_name);
                EditText input_password =  findViewById(R.id.input_password);

                String username = input_name.getText().toString();
                String password = input_password.getText().toString();

                if(username.isEmpty() || password.isEmpty()) {
                    GeneralUtil.showToast(SignInActivity.this,"Invalid Credentials. Please enter correct values.");
                    return;
                }

                HashMap<String, String> user = new HashMap<>();
                user.put("username","gmonelabs");
                user.put("password","Pay729@321");


                ApiInterface apiService =
                        ApiClient.getClient().create(ApiInterface.class);
                Call<LoginResponse> call = apiService.doLogin(ApiClient.X_API_KEY,user);
                call.enqueue(SignInActivity.this);

            }
            break;
        }
    }

    @Override
    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
        String status = response.body().getStatus();
        String message = response.body().getMessage();
        String first_name = response.body().getFirst_name();
        String last_name = response.body().getLast_name();

        Log.i("onResponse","status:"+status+" Message:"+message+" first_name:"+first_name+" last_name:"+last_name);

        if(status!=null && status.equalsIgnoreCase("1"))
        {
            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onFailure(Call<LoginResponse> call, Throwable t) {
        t.printStackTrace();
    }
}
