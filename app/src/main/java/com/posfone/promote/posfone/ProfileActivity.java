package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvc.imagepicker.ImagePicker;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, Callback {


    @BindView(R.id.profile_image)
    ImageView profile_image;

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

        loadProfileDetails();

        //ImagePicker.setMinQuality(200, 200);

    }

    private void loadProfileDetails()
    {
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);

        ((TextView)findViewById(R.id.txt_user_name)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME)+" "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView)findViewById(R.id.txt_user_location)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE)+","+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY));

        ((TextView)findViewById(R.id.txt_user_phone)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_pay_number)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER)+"");
        ((TextView)findViewById(R.id.txt_email_address)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL)+"");


        //Load New Image in Profile Pic
        String profile_pic_url = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO);
        if(profile_pic_url!=null && !profile_pic_url.isEmpty())
        Picasso.with(ProfileActivity.this).load(profile_pic_url).into(profile_image);
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
        Log.i("ProfileActivity",requestCode +" "+resultCode);
        if(requestCode==1001 && resultCode==RESULT_OK)
        {
            //Update Profile Data from Server
            loadProfileDetails();
        }else if(requestCode ==234 && resultCode == RESULT_OK)
        {
            //ImagePicker Pick Image Default request code
            String file_path = ImagePicker.getImagePathFromResult(ProfileActivity.this,requestCode,resultCode,data);
            Log.i("ProfileActivity",file_path);
            File file = new File(file_path);
            uploadProfileImage(file);
        }
    }


    private void uploadProfileImage(File file)
    {
        GeneralUtil.showProgressDialog(this,"Please wait...");

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        RequestBody formBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile_photo", file.getName(),
                            RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

        Call call = RESTClient.uploadPhoto(header, formBody, this);
    }

    @Override
    public void onFailure(Call call, IOException e) {
        GeneralUtil.dismissProgressDialog();
        Log.i("ProfileActivity","onFailure");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        GeneralUtil.dismissProgressDialog();
        Log.i("ProfileActivity","onResponse");

        try {

            String res = response.body().string();
            Log.i("onResponse",res);
            final JSONObject jsonObject = new JSONObject(res);
            String message = jsonObject.getString("message");
            final String profile_photo = jsonObject.getString("profile_photo");
            if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Save Profile Details

                        final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);
                        preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO,profile_photo);

                        //Load New Image in Profile Pic
                        Picasso.with(ProfileActivity.this).load(profile_photo).into(profile_image);
                    }
                });
            }else
            {
                GeneralUtil.showToast(ProfileActivity.this,message);
            }

        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
