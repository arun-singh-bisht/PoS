package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;


public class ChoosePlanActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);

        initViews();
        getPackages();
    }

    private void initViews()
    {
        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Choose Number");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setVisibility(View.GONE);
        //findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.get_free_number).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.get_free_number:{
                startActivity(new Intent(ChoosePlanActivity.this,ChooseNumberActivity.class));
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ChoosePlanActivity.this,PreSignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void getPackages()
    {
        //Show loading dialog
        final AlertDialog progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait")
                .build();
        progressDialog.show();

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);


        Call call = RESTClient.call_POST(RESTClient.PACKAGES, header, "", new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                if(progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();


                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    //-----------
                }

            }
        });
    }
}
