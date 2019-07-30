package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EidtProfileActivity extends AppCompatActivity implements View.OnClickListener , Callback{

    private final int ACTION_FOR_COUNTRY = 1001;
    private final int ACTION_FOR_STATE = 1002;
    String selectedCountry;
    String selectedState;
    String selectedCountryFlag;
    private String selectedCountryPhoneCode;

    @BindView(R.id.input_fname)
    EditText input_fname;
    @BindView(R.id.input_lname)
    EditText input_lname;
    @BindView(R.id.input_company_name)
    EditText input_company_name;
    @BindView(R.id.input_company_address)
    EditText input_company_address;
    @BindView(R.id.input_country)
    EditText input_country;
    @BindView(R.id.input_state)
    EditText input_state;
    @BindView(R.id.input_city)
    EditText input_city;
    @BindView(R.id.input_postcode)
    EditText input_postcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews()
    {
        ((TextView)findViewById(R.id.txt_title)).setText("Edit Profile");
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.img_right).setVisibility(View.GONE);

        findViewById(R.id.input_country).setOnClickListener(this);
        findViewById(R.id.input_state).setOnClickListener(this);
        findViewById(R.id.btn_next).setOnClickListener(this);


        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(EidtProfileActivity.this);
        GeneralUtil.setValueInEditText(input_fname,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME));
        GeneralUtil.setValueInEditText(input_lname,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        GeneralUtil.setValueInEditText(input_company_name,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_COMPANY_NAME));
        GeneralUtil.setValueInEditText(input_company_address,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_COMPANY_ADDRESS));
        GeneralUtil.setValueInEditText(input_country,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY));
        GeneralUtil.setValueInEditText(input_state,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE));
        GeneralUtil.setValueInEditText(input_city,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_CITY));
        GeneralUtil.setValueInEditText(input_postcode,preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_POSTCODE));


        selectedCountry = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
            case R.id.btn_next:{
                signUp();
            }
            break;
            case R.id.input_country:{

                Intent intent = new Intent(EidtProfileActivity.this,SearchCountryActivity.class);
                intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
                startActivityForResult(intent,ACTION_FOR_COUNTRY);

            }
            break;
            case R.id.input_state:{

                Intent intent = new Intent(EidtProfileActivity.this,SearchCountryActivity.class);
                SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
                if (selectedCountryPhoneCode == null)
                    selectedCountryPhoneCode = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY_CODE);
                    selectedCountryFlag= preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY_FLAG);
                Log.e("Details",selectedCountry+" -> "+selectedCountryPhoneCode+" -> "+selectedCountryFlag);
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

        GeneralUtil.showProgressDialog(this,"Please wait...");

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        String token = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_TOKEN);


        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        header.put("token", token);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("first_name", input_fname.getText().toString());
        jsonObject.addProperty("last_name",input_lname.getText().toString());
        jsonObject.addProperty("company_name",input_company_name.getText().toString());
        jsonObject.addProperty("address",input_company_address.getText().toString());
        jsonObject.addProperty("country",input_country.getText().toString());
        jsonObject.addProperty("city",input_city.getText().toString());
        jsonObject.addProperty("zipcode",input_postcode.getText().toString());
        String state = input_state.getText().toString();
        if(state.equalsIgnoreCase("Please select"))
            state = "";
        jsonObject.addProperty("state",state);
        jsonObject.addProperty("email",preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL));
        jsonObject.addProperty("idenity",preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_NAME));
        //jsonObject.addProperty("password",preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY)));

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.EDIT_PROFILE, header, body, this);

    }

    private boolean validateData()
    {
        String message = null;

        String input_country =  GeneralUtil.getTextFromEditText(this,R.id.input_country);
        String input_state =  GeneralUtil.getTextFromEditText(this,R.id.input_state);

        if(!GeneralUtil.validateEditText(this,R.id.input_fname))
            message = "Enter First name.";
        else if(input_country.equalsIgnoreCase("Please select"))
            message = "Please select your Country.";

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

            try {

                String res = response.body().string();
                Log.i("onResponse",res);
                JSONObject jsonObject = new JSONObject(res);
                String message = jsonObject.getString("message");
                if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //Save Profile Details
                            final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(EidtProfileActivity.this);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY,input_country.getText().toString());
                            String state = input_state.getText().toString();
                            if(state.equalsIgnoreCase("Please select"))
                                state = "";
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE,state);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME,input_fname.getText().toString());
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME,input_lname.getText().toString());
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_COMPANY_NAME,input_company_name.getText().toString());
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_COMPANY_ADDRESS,input_company_address.getText().toString());
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_CITY,input_city.getText().toString());
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_POSTCODE,input_postcode.getText().toString());

                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                }else
                {
                    GeneralUtil.showToast(EidtProfileActivity.this,message);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //-----
            }
    }
}
