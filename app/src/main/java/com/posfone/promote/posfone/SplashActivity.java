package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;


public class SplashActivity extends AppCompatActivity {


    private String lastCountryFlagName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(SplashActivity.this);
                        if(preferenceHandler.getBooleanValue(SharedPreferenceHandler.SP_KEY_IS_LOGIN))
                        {
                            startActivity(new Intent(SplashActivity.this,MainActivity.class));
                        }else
                        {
                            startActivity(new Intent(SplashActivity.this,PreSignInActivity.class));
                        }
                        finish();
                    }
                },1200);


    }

}
