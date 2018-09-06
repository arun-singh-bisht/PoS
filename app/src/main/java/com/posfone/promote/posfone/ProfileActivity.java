package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
    }

    private void initViews()
    {
        //Back arrow
        findViewById(R.id.img_left).setOnClickListener(this);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);

        ((TextView)findViewById(R.id.txt_user_name)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME)+" "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView)findViewById(R.id.txt_user_location)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE)+","+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY));

        ((TextView)findViewById(R.id.txt_user_phone)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_pay_number)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_email_address)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL)+"");
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

}
