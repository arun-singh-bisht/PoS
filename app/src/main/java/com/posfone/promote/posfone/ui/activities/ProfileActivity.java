package com.posfone.promote.posfone.ui.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.sip.SipManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mvc.imagepicker.ImagePicker;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

    @BindView(R.id.indeterminateBar)
    ProgressBar indeterminateBar;
    @BindView(R.id.lable_hh_mm_ss_bundle_min)
    TextView bundle_min;
    @BindView(R.id.lable_hh_mm_ss_call_min)
    TextView call_min;
    @BindView(R.id.lable_hh_mm_ss_remaining_time)
    TextView remaining_time;
    final int PIC_CROP = 1;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        initViews();
    }

    @SuppressLint("HandlerLeak")
    private void initViews() {
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);
        call_min.setText( preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_CALL_MIN));
       remaining_time.setText( preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_REMAINING_TIME));
        bundle_min.setText( preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_BUNDLE_MIN));
        //Back arrow
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.img_right).setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                //Set Empty Image in Image View
                profile_image.setImageResource(R.drawable.blank_profile_image);
                //Show progress bar
                indeterminateBar.setVisibility(View.VISIBLE);

                String image_path = msg.getData().getString("image_path");
                Log.i("handleMessage", image_path);
                if (image_path != null && !image_path.isEmpty())
                    Picasso.with(ProfileActivity.this)
                            .load(image_path)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(profile_image, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    indeterminateBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    indeterminateBar.setVisibility(View.GONE);
                                    Toast.makeText(ProfileActivity.this, "Error in Image updation.", Toast.LENGTH_SHORT).show();
                                }
                            });

            }
        };

        loadProfileDetails();

        //ImagePicker.setMinQuality(200, 200);
        Picasso.with(this).setLoggingEnabled(true);
    }

    private void loadProfileDetails() {
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);
        String address = "";
        String city = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_CITY);
        String state = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE);
        String country = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY);
        String postcode = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_POSTCODE);

        if (city != null && city.length() > 0 && !city.equalsIgnoreCase("null"))
            address = city + ",";
        if (state != null && state.length() > 0 && !state.equalsIgnoreCase("null"))
            address = address + "" + state + ",";
        if (country != null && country.length() > 0 && !country.equalsIgnoreCase("null"))
            address = address + "" + country;
        if (postcode != null && postcode.length() > 0 && !postcode.equalsIgnoreCase("null"))
            address = address + "," + postcode;

        ((TextView) findViewById(R.id.txt_user_name)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME) + " " + preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView) findViewById(R.id.txt_user_location)).setText(address);

        ((TextView)findViewById(R.id.txt_user_phone)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER));
        ((TextView)findViewById(R.id.txt_pay_number)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER));
        ((TextView)findViewById(R.id.txt_email_address)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL));
        ((TextView)findViewById(R.id.txt_plan_type)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PACKAGE_NAME));
        ((TextView)findViewById(R.id.txt_plan_exire_date)).setText("Expire - "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PACKAGE_EXPIRE_DATE));

        //Load New Image in Profile Pic
        String profile_pic_url = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO);
        if(profile_pic_url!=null && !profile_pic_url.isEmpty())
        Picasso.with(ProfileActivity.this)
                .load(profile_pic_url)
                .placeholder(R.drawable.blank_profile_image)
                .into(profile_image);
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
         Log.i("onProfileImageClick","isApiSupported:"+SipManager.isApiSupported(this));
        Log.i("onProfileImageClick","isVoipSupported:"+SipManager.isVoipSupported(this));

       // SipController sipController = new SipController();
        //sipController.initSip(this);
        //sipController.createSipProfile(null,null,null);
    }

    @OnClick(R.id.btn_upgrade_plan)
    public void onUpgrade()
    {
        //Show Confirmation Dialog before making a call
        CustomAlertDialog.showDialog(this, "Do you wish to retain your number?","Yes","No, I want to change my number", R.layout.custom_dialog_upgrade, new CustomAlertDialog.I_CustomAlertDialog() {
            @Override
            public void onPositiveClick() {
                Intent intent = new Intent(ProfileActivity.this, PackageActivity.class);
                intent.putExtra("redirect_from","profile_screen");
                intent.putExtra("is_change_number","0");
                intent.putExtra("isTrial","0");
                startActivity(intent);
                finish();
            }

            @Override
            public void onNegativeClick() {
                /*Intent intent = new Intent(ProfileActivity.this,ChooseNumberActivity.class);
                intent.putExtra("redirect_from","profile_screen");
                intent.putExtra("isTrial","0");
                startActivity(intent);
                finish();*/

                Intent intent = new Intent(ProfileActivity.this, PackageActivity.class);
                intent.putExtra("redirect_from","profile_screen");
                intent.putExtra("is_change_number","1");
                intent.putExtra("isTrial","0");
                startActivity(intent);
                finish();
            }
        });


    }


    @OnClick(R.id.txt_see_other_bundles)
    public void onSeeOtherBundles()
    {
        Intent intent = new Intent(ProfileActivity.this, BundlesListActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {

            Log.i("ProfileActivity", requestCode + " " + resultCode);
            if (requestCode == 1001 && resultCode == RESULT_OK) {
                //Update Profile Data from Server
                loadProfileDetails();
            } else if (requestCode == 234 && resultCode == RESULT_OK) {
                //ImagePicker Pick Image Default request code
                String file_path = ImagePicker.getImagePathFromResult(ProfileActivity.this, requestCode, resultCode, data);
                Log.i("ProfileActivity", file_path);
                File file = new File(file_path);

                Uri uri = FileProvider.getUriForFile(this,"com.mvc.imagepicker.provider",file);
                Log.i("ProfileActivity", uri.toString());
                performCrop(uri);
                //int orientation = ImageUtil.getImageRotation(ProfileActivity.this, uri);

                //Log.i("ProfileActivity", "orientation "+orientation);

                /*if (bm != null)
                    profile_image.setImageBitmap(bm);*/

               // uploadProfileImage(file);
            } else if (requestCode == PIC_CROP) {
                if (data != null) {
                    // get the returned data
                    Bundle extras = data.getExtras();
                    // get the cropped bitmap
                    Bitmap selectedBitmap = extras.getParcelable("data");
                   // Log.e("ProfileActivityimage", getFile(selectedBitmap).getAbsolutePath());
                    uploadProfileImage(getFile(selectedBitmap));
                }
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private File getFile(Bitmap bitmap)throws Exception {
        //create a file to write bitmap data
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        File f = new File(this.getCacheDir(), "profile_image_"+userID+".png");
        f.createNewFile();

//Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        Log.e("onupload",f.getAbsolutePath());
        return f;
    }

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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
                    .addFormDataPart("profile_photo", file.getName(),RequestBody.create(MediaType.parse("image/png"), file))
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


                //Save Profile Details
                Log.i("runOnUiThread",profile_photo);

                final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ProfileActivity.this);
                preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO,profile_photo);

                Message message1 = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("image_path",profile_photo);
                message1.setData(bundle);
                handler.sendMessage(message1);


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
