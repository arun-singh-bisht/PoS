package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ACTION_FOR_COUNTRY = 1001;
    private final int ACTION_FOR_STATE = 1002;
    String selectedCountry;
    String selectedState;
    String selectedCountryFlag;
    private String selectedCountryPhoneCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
    }

    private void initViews()
    {
        findViewById(R.id.input_country).setOnClickListener(this);
        findViewById(R.id.input_state).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_next:{
                signUp();
            }
            break;
            case R.id.input_country:{

                Intent intent = new Intent(SignUpActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY);

            }
            break;
            case R.id.input_state:{

                Intent intent = new Intent(SignUpActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,selectedCountry);
                intent.putExtra("selectedCountryPhoneCode",selectedCountryPhoneCode);
                intent.putExtra("selectedCountryFlag",selectedCountryFlag);
                startActivityForResult(intent,ACTION_FOR_STATE);

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case ACTION_FOR_COUNTRY:
                {
                    selectedCountry = data.getStringExtra("result");
                    selectedCountryFlag = data.getStringExtra("selectedCountryFlag");
                    selectedCountryPhoneCode = data.getStringExtra("selectedCountryPhoneCode");
                    EditText editText = findViewById(R.id.input_country);
                    editText.setText(selectedCountry);

                    selectedState = null;
                    EditText editText_state = findViewById(R.id.input_state);
                    editText_state.setText("Please select");

                }break;
                case ACTION_FOR_STATE:
                {
                    selectedState = data.getStringExtra("result");
                    EditText editText = findViewById(R.id.input_state);
                    editText.setText(selectedState);
                }break;
            }
        }
    }


    private void signUp()
    {
        //Show loading dialog
        final AlertDialog  progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait")
                .build();
        progressDialog.show();

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", ((EditText)findViewById(R.id.input_fname)).getText().toString());
        jsonObject.addProperty("last_name",((EditText)findViewById(R.id.input_lname)).getText().toString());
        jsonObject.addProperty("company_name",((EditText)findViewById(R.id.input_company_name)).getText().toString());
        jsonObject.addProperty("address",((EditText)findViewById(R.id.input_company_address)).getText().toString());
        jsonObject.addProperty("country",((EditText)findViewById(R.id.input_country)).getText().toString());
        jsonObject.addProperty("state",((EditText)findViewById(R.id.input_state)).getText().toString());
        jsonObject.addProperty("email",((EditText)findViewById(R.id.input_email)).getText().toString());
        jsonObject.addProperty("identity",((EditText)findViewById(R.id.input_username)).getText().toString());
        jsonObject.addProperty("password",((EditText)findViewById(R.id.input_password)).getText().toString());

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.SIGN_UP, header, body, new okhttp3.Callback() {
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

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SignUpActivity.this,ChoosePlanActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            });
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
