package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener , Callback{

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

        if(!validateData())
            return;

        GeneralUtil.showProgressDialog(this,"Sign Up in progress...");

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
        jsonObject.addProperty("idenity",((EditText)findViewById(R.id.input_username)).getText().toString());
        jsonObject.addProperty("password",((EditText)findViewById(R.id.input_password)).getText().toString());

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.SIGN_UP, header, body, this);

    }

    private boolean validateData()
    {
        String message = null;

        String input_country =  GeneralUtil.getTextFromEditText(this,R.id.input_country);
        String input_state =  GeneralUtil.getTextFromEditText(this,R.id.input_state);

       if(!GeneralUtil.validateEditText(this,R.id.input_fname))
           message = "Enter First Name.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_lname))
           message = "Enter Last Name.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_company_name))
           message = "Enter Company Name.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_company_address))
           message = "Enter Company Address.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_email))
           message = "Enter Email Address.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_username))
           message = "Enter Username.";
       else if(!GeneralUtil.validateEditText(this,R.id.input_password))
           message = "Enter Password.";
       else if(input_country.equalsIgnoreCase("Please select"))
           message = "Please select your Country.";
       else if(input_state.equalsIgnoreCase("Please select"))
           message = "Please select your State.";

       if(message != null)
       {
           GeneralUtil.showToast(this,message);
           return false;
       }

        return true;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        GeneralUtil.dismissProgressDialog();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

        GeneralUtil.dismissProgressDialog();

        if (response.isSuccessful()) {
            try {

                String res = response.body().string();
                Log.i("onResponse",res);
                JSONObject jsonObject = new JSONObject(res);
                String message = jsonObject.getString("message");
                if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                    //Save User ID in SP.
                    //new SharedPreferenceHandler(SignUpActivity.this).putValue(SharedPreferenceHandler.SP_KEY_USER_ID,jsonObject.getString("user_id"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            finish();
                            //GeneralUtil.showToast(SignUpActivity.this,"SignUp Successfull.");
                            Intent intent = new Intent(SignUpActivity.this,SignInActivity.class);
                            intent.putExtra("username",((EditText)findViewById(R.id.input_username)).getText().toString());
                            intent.putExtra("password",((EditText)findViewById(R.id.input_password)).getText().toString());
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }
                    });
                }else
                {
                    GeneralUtil.showToast(SignUpActivity.this,message);
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
}
