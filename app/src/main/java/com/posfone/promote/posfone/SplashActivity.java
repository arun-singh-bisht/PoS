package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.posfone.promote.posfone.Utils.PicasoImageUtil;
import com.posfone.promote.posfone.database.DAO;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.GetCountryResponse;
import com.posfone.promote.posfone.model.LoginResponse;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {


    private String lastCountryFlagName = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this,PreSignInActivity.class));
                        finish();
                    }
                },1000*2);


    }

}
