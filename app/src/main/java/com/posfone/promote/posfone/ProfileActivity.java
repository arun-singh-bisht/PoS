package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews()
    {
        //Back arrow
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.img_right).setOnClickListener(this);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);

        ((TextView)findViewById(R.id.txt_user_name)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME)+" "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView)findViewById(R.id.txt_user_location)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE)+","+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY));

        ((TextView)findViewById(R.id.txt_user_phone)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_pay_number)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_email_address)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL)+"");

        ImagePicker.setMinQuality(200, 200);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
            case R.id.img_right:{
                startActivityForResult(new Intent(ProfileActivity.this,EidtProfileActivity.class),1001);
            }
            break;
        }
    }


    @OnClick(R.id.profile_image)
    public void onProfileImageClick()
    {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && resultCode==RESULT_OK)
        {
            //Update Profile Data from Server
        }else if(requestCode ==234 && requestCode == RESULT_OK)
        {

        }
    }
}
