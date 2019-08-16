package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.PermissionUtils;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;

import butterknife.OnClick;


public class PreSignInActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_in);

        initViews();

        /*Asking Permissions*/
        new PermissionUtils().requestForPermission(this);
    }

    private void initViews() {
        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_free_trial).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_sign_in: {

                SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(PreSignInActivity.this);
                String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
                userID = null;
                if (userID == null) {
                    startActivity(new Intent(PreSignInActivity.this, SignInActivity.class));
                } else {
                    //Open ChoosePlanActivity screen
                    Intent intent = new Intent(PreSignInActivity.this, ChoosePlanActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
            break;
            case R.id.btn_free_trial: {
                startActivity(new Intent(PreSignInActivity.this, SignUpActivity.class));
                //startActivity(new Intent(PreSignInActivity.this,WebViewActivity.class));
            }
            break;
        }
    }



    @OnClick(R.id.terms_of_service)
    public void load_terms_of_service() {
        load_webview("https://gmonelabs.pay729.guru/terms_conditions");
    }
    @OnClick(R.id.terms_of_privacy)
    public void load_terms_of_privacy() {
        load_webview("https://gmonelabs.pay729.guru/privacy_policy");
    }

    public void load_webview(String url) {
        Intent intent =new Intent(PreSignInActivity.this,WebViewActivity.class);
        intent.putExtra("data",url);
        startActivity(intent);
    }
}
