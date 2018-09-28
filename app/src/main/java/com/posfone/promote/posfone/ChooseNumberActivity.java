package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.fragment.NumberFragment;
import com.posfone.promote.posfone.fragment.PaymentFragment;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.PackageModel;
import com.posfone.promote.posfone.model.TwilioNumber;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class ChooseNumberActivity extends AppCompatActivity  {

    private final int ACTION_FOR_COUNTRY = 1001;
    private String redirect_from;
    private String isTrial ="1";
    private ViewPager viewPager;
    private NumberFragment numberFragment_type_regular;
    private NumberFragment numberFragment_type_premium;
    private NumberFragment numberFragment_type_elite;
    //private PackageModel packageModel;
    List<TwilioNumber> twilioNumbers_regular = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_premium = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_elite = new ArrayList<>();

    @BindView(R.id.txt_select_country)
    TextView currenCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews()
    {
        redirect_from =  getIntent().getStringExtra("redirect_from");

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Choose Number");

        //((ImageView)findViewById(R.id.img_right)).setImageResource(R.mipmap.ic_language);
        findViewById(R.id.img_right).setVisibility(View.GONE);
        if(redirect_from!=null && redirect_from.equalsIgnoreCase("profile_screen")) {
            findViewById(R.id.img_left).setVisibility(View.VISIBLE);
            isTrial = "0";
        }
        else
            findViewById(R.id.img_left).setVisibility(View.GONE);


        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        getTwilioNumber(null);

    }

    @OnClick(R.id.img_left)
    public void onBackArrowClick()
    {
        finish();
    }


    @Override
    public void onBackPressed() {

        if(redirect_from!=null && redirect_from.equalsIgnoreCase("profile_screen"))
        {
            finish();
        }else {
            Intent intent = new Intent(ChooseNumberActivity.this, PreSignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @OnClick(R.id.txt_select_country)
    public void onSelectCountryClick()
    {
        Intent intent = new Intent(ChooseNumberActivity.this,SearchCountryActivity.class);
        intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
        startActivityForResult(intent,ACTION_FOR_COUNTRY);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Tab Regular
        numberFragment_type_regular = new NumberFragment();
        numberFragment_type_regular.setFragmentName("Regular");
        adapter.addFrag(numberFragment_type_regular, "Regular");
        //Tab Premium
        numberFragment_type_premium = new NumberFragment();
        numberFragment_type_premium.setFragmentName("Premium");
        adapter.addFrag(numberFragment_type_premium, "Premium");
        //Tab Elite
        numberFragment_type_elite = new NumberFragment();
        numberFragment_type_elite.setFragmentName("Elite");
        adapter.addFrag(numberFragment_type_elite, "Elite");


        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("Fragment getItem "+position);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            System.out.println("Fragment addFrag "+title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            System.out.println("Fragment getPageTitle "+position);
            return mFragmentTitleList.get(position);
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

                    CountryModel countryModel = new CountryModel();
                    countryModel.name = data.getStringExtra("result");
                    countryModel.iso = data.getStringExtra("selectedCountryIso");
                    countryModel.phonecode = data.getStringExtra("selectedCountryPhoneCode");

                    currenCountry.setText(countryModel.name);
                    getTwilioNumber(countryModel);
                }
                break;
            }
        }
    }


    private void getTwilioNumber(final CountryModel selectedCountry)
    {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,"Please wait");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        if(selectedCountry==null)
        header.put("userid", userID);
        //RequestBody
        String body = "";
        if(selectedCountry!=null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("country", selectedCountry.getIso());
            jsonObject.addProperty("area_code", selectedCountry.getPhonecode());
            body = "json=" + jsonObject.toString();
        }

        Log.i("getTwilioNumber",userID+" "+body);

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                twilioNumbers_regular.clear();
                twilioNumbers_premium.clear();
                twilioNumbers_elite.clear();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {


                            final String country_name = jsonObject.getString("country_name");
                            JSONObject phone_number = jsonObject.getJSONObject("phone_number");

                            //Add Default Number

                            if(phone_number.has("default"))
                            {
                                JSONArray jsonArray_default = phone_number.getJSONArray("default");
                                for(int k = 0;k<jsonArray_default.length();k++)
                                {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object =  jsonArray_default.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type ="Regular";
                                    twilioNumbers_regular.add(twilioNumber);
                                }
                            }

                            //Add premium Number
                            if(phone_number.has("premium"))
                            {
                                JSONArray jsonArray_premium = phone_number.getJSONArray("premium");
                                for(int k = 0;k<jsonArray_premium.length();k++)
                                {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object =  jsonArray_premium.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type ="premium";
                                    twilioNumbers_premium.add(twilioNumber);
                                }
                            }

                            //Add elite Number
                            if(phone_number.has("elite"))
                            {
                                JSONArray jsonArray_elite = phone_number.getJSONArray("elite");
                                for(int k = 0;k<jsonArray_elite.length();k++)
                                {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object =  jsonArray_elite.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type ="elite";
                                    twilioNumbers_elite.add(twilioNumber);
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        currenCountry.setText(country_name);
                                        numberFragment_type_regular.setData(twilioNumbers_regular);
                                        numberFragment_type_premium.setData(twilioNumbers_premium);
                                        numberFragment_type_elite.setData(twilioNumbers_elite);
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


    public void selectTwilioNumber(String numberType,String number) throws JSONException {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ChooseNumberActivity.this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        JSONObject jsonNumber = new JSONObject();
        jsonNumber.put("number",number);

        JSONArray jsonArrayNumber = new JSONArray();
        jsonArrayNumber.put(jsonNumber);

        JSONObject jsonNumberType = new JSONObject();
        jsonNumberType.put(numberType,jsonArrayNumber);

        JSONObject jsonTwillioNumber = new JSONObject();
        jsonTwillioNumber.put("twillio_nos",jsonNumberType);

        String body = "json="+jsonTwillioNumber.toString();
        Log.i("NumberFragment",body);

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER_SELECT, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();


                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ChooseNumberActivity.this, PackageActivity.class);
                                    intent.putExtra("isTrial",isTrial);

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
