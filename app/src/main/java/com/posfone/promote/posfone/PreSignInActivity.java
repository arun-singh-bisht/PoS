package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.posfone.promote.posfone.Utils.PermissionUtils;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;


public class PreSignInActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_sign_in);

        initViews();

        /*Asking Permissions*/
        new PermissionUtils().requestForPermission(this);
    }

    private void initViews()
    {
        findViewById(R.id.btn_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_free_trial).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_sign_in:{

                SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(PreSignInActivity.this);
                String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
                userID = null;
                        if(userID==null)
                        {
                            startActivity(new Intent(PreSignInActivity.this,SignInActivity.class));
                        }else
                        {
                            //Open ChoosePlanActivity screen
                            Intent intent = new Intent(PreSignInActivity.this,ChoosePlanActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
            }
            break;
            case R.id.btn_free_trial:{
                startActivity(new Intent(PreSignInActivity.this,SignUpActivity.class));
                //finish();
            }
            break;
        }
    }
}
