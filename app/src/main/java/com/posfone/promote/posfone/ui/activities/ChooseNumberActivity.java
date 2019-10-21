package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.models.CountryModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.models.NubmerCategoryModel;
import com.posfone.promote.posfone.data.remote.models.TwilioNumber;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.fragments.FilterBottomDialogFragment;
import com.posfone.promote.posfone.ui.fragments.NumberFragment;

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


public class ChooseNumberActivity extends AppCompatActivity implements FilterBottomDialogFragment.FilterBottomDialogFragmentInterface {

    private final int ACTION_FOR_COUNTRY = 1001;
    private HashMap<String, String> packageModel;
    private String redirect_from;
    private String isTrial = "1";
    private ViewPager viewPager;
    public static String country_name_search = "";
    public static String country_code_search = "";
    public static String country_iso_search = "";

    public static NumberFragment numberFragment_type_regular;
    public static NumberFragment numberFragment_type_premium;
    public static NumberFragment numberFragment_type_elite;

    List<TwilioNumber> twilioNumbers_regular = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_premium = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_elite = new ArrayList<>();

    @BindView(R.id.txt_select_country)
    TextView currenCountry;

    private String filter_contactNumber ="";
    private String filter_areaCode ="";
    private String filter_countryIso ="";
    private String filter_countryName ="TAP";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        redirect_from = getIntent().getStringExtra("redirect_from");
        isTrial = getIntent().getStringExtra("isTrial");
        packageModel = (HashMap<String, String>) getIntent().getExtras().getSerializable("SelectedPackage");


        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Choose Number");

        //((ImageView)findViewById(R.id.img_right)).setImageResource(R.mipmap.ic_language);

        findViewById(R.id.img_left).setVisibility(View.VISIBLE);
        ImageView img_right = (ImageView) findViewById(R.id.img_right);
        img_right.setVisibility(View.VISIBLE);
        img_right.setImageResource(R.drawable.filter_button);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        getTwilioNumber(null);
        //getPackages();

    }

    @OnClick(R.id.img_left)
    public void onBackArrowClick() {
        finish();
    }

    @OnClick(R.id.img_right)
    public void onFilterIconClick() {
        openBottomFilterSheet();
    }


    @Override
    public void onBackPressed() {

        if (redirect_from != null && redirect_from.equalsIgnoreCase("profile_screen")) {
            finish();
        } else {
            // Intent intent = new Intent(ChooseNumberActivity.this, PreSignInActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.txt_select_country)
    public void onSelectCountryClick() {
        Intent intent = new Intent(ChooseNumberActivity.this, SearchCountryActivity.class);
        intent.putExtra(SearchCountryActivity.TAG_TYPE, SearchCountryActivity.TAG_COUNTRY);
        startActivityForResult(intent, ACTION_FOR_COUNTRY);
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
            System.out.println("Fragment getItem " + position);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            System.out.println("Fragment addFrag " + title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            System.out.println("Fragment getPageTitle " + position);
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            System.out.println("---------------------------------");
            switch (requestCode) {
                case ACTION_FOR_COUNTRY: {
                    CountryModel countryModel = new CountryModel();
                    countryModel.name = data.getStringExtra("result");
                    countryModel.iso = data.getStringExtra("selectedCountryIso");
                    countryModel.phonecode = data.getStringExtra("selectedCountryPhoneCode");
                    country_name_search = data.getStringExtra("result");
                    country_code_search = data.getStringExtra("selectedCountryPhoneCode");
                    country_iso_search = data.getStringExtra("selectedCountryIso");
                    currenCountry.setText(countryModel.name);

                    filter_countryName = countryModel.name;
                    filterBottomDialogFragment.setInput_CountryName(filter_countryName);
                    getTwilioNumber(countryModel);
                }
                break;
            }
        }
    }


    private void getTwilioNumber(final CountryModel selectedCountry) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this, "Please wait");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
//        if (selectedCountry == null)
//            header.put("userid", userID);
        //RequestBody
        String body = "";
        JsonObject jsonObject = new JsonObject();
        if (selectedCountry != null) {

            jsonObject.addProperty("country", selectedCountry.getIso());
            jsonObject.addProperty("area_code", "");
            jsonObject.addProperty("filter", "");
        } else {
            jsonObject.addProperty("country", "GB");
            jsonObject.addProperty("area_code", "1608");
            jsonObject.addProperty("filter", "");
            jsonObject.addProperty("sample_number", "+441424400293");

        }
        jsonObject.addProperty("package_id", packageModel.get("packageId"));
        body = "json=" + jsonObject.toString();

        Log.i("getTwilioNumber", userID + " " + body);

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                twilioNumbers_regular.clear();
                twilioNumbers_premium.clear();
                twilioNumbers_elite.clear();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            final String country_name = jsonObject.getString("country_name");
                            JSONObject phone_number = jsonObject.getJSONObject("phone_number");
                            final String country_code = jsonObject.getString("country_code");

                            //-------------
                            country_name_search = country_name;
                            country_code_search = country_code;
                            country_iso_search = country_code;
                            //-----------

                            //Add Default Number

                            if (phone_number.has("default")) {
                                JSONArray jsonArray_default = phone_number.getJSONArray("default");
                                for (int k = 0; k < jsonArray_default.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_default.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "Regular";
                                    twilioNumbers_regular.add(twilioNumber);
                                }
                            }

                            //Add premium Number
                            if (phone_number.has("premium")) {
                                JSONArray jsonArray_premium = phone_number.getJSONArray("premium");
                                for (int k = 0; k < jsonArray_premium.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_premium.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "premium";
                                    twilioNumbers_premium.add(twilioNumber);
                                }
                            }

                            //Add elite Number
                            if (phone_number.has("elite")) {
                                JSONArray jsonArray_elite = phone_number.getJSONArray("elite");
                                for (int k = 0; k < jsonArray_elite.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_elite.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "elite";
                                    twilioNumbers_elite.add(twilioNumber);
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (selectedCountry == null)
                                        currenCountry.setText("Select Country");
                                    else
                                        currenCountry.setText(country_name);
                                       /* numberFragment_type_regular.setData(twilioNumbers_regular);
                                        numberFragment_type_premium.setData(twilioNumbers_premium);
                                        numberFragment_type_elite.setData(twilioNumbers_elite);*/
                                    display_numbers(twilioNumbers_regular, twilioNumbers_premium, twilioNumbers_elite);
                                }
                            });


                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    display_numbers(twilioNumbers_regular, twilioNumbers_premium, twilioNumbers_elite);
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

    //----------------------
    public boolean isValid() {
        return country_name_search != "" && country_code_search != "";
    }

    //----------------

    public void selectTwilioNumber(final String numberType, final String number) throws JSONException {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this, null);

        final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(ChooseNumberActivity.this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        JSONObject jsonNumber = new JSONObject();
        jsonNumber.put("number", number);

        JSONArray jsonArrayNumber = new JSONArray();
        jsonArrayNumber.put(jsonNumber);

        JSONObject jsonNumberType = new JSONObject();
        jsonNumberType.put(Uri.encode(numberType), jsonArrayNumber);

        JSONObject jsonTwillioNumber = new JSONObject();
        jsonTwillioNumber.put("twillio_nos", jsonNumberType);

        String body = "json=" + jsonTwillioNumber.toString();

        Log.i("NumberFragment", body);

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER_SELECT, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();


                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ChooseNumberActivity.this, SummeryActivity.class);
                                    if ("profile_screen".equals(redirect_from)) {
                                        System.out.println("new number selected-----------  " + number);
                                        preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_NEW_PAY_729_NUMBER, numberType);
                                    }
                                    intent.putExtra("redirect_from", redirect_from);
                                    intent.putExtra("isTrial", isTrial);
                                    Bundle extras = new Bundle();
                                    extras.putSerializable("SelectedPackage", packageModel);
                                    intent.putExtras(extras);
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

    public void display_numbers(List<TwilioNumber> twilioNumbers_regular, List<TwilioNumber> twilioNumbers_premium, List<TwilioNumber> twilioNumbers_elite) {
        numberFragment_type_regular.setData(twilioNumbers_regular, packageModel.get("packageId"));
        numberFragment_type_premium.setData(twilioNumbers_premium, packageModel.get("packageId"));
        numberFragment_type_elite.setData(twilioNumbers_elite, packageModel.get("packageId"));
    }


    private void getPackages() {
        //Show loading dialog
        GeneralUtil.showProgressDialog(this, null);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("package_id", packageModel.get("packageId"));
        String body = "json=" + jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.PLANS, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        JSONObject jsonObject = new JSONObject(res);

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

    FilterBottomDialogFragment filterBottomDialogFragment;

    private void openBottomFilterSheet() {
        filterBottomDialogFragment =
                FilterBottomDialogFragment.newInstance(this);
        filterBottomDialogFragment.setInput_ContactNumber(filter_contactNumber);
        filterBottomDialogFragment.setInput_AreaCode(filter_areaCode);
        filterBottomDialogFragment.setInput_CountryName(filter_countryName);
        filterBottomDialogFragment.show(getSupportFragmentManager(),
                "add_photo_dialog_fragment");

    }

    @Override
    public void onFilterShowCountryList() {
        Intent intent = new Intent(ChooseNumberActivity.this, SearchCountryActivity.class);
        intent.putExtra(SearchCountryActivity.TAG_TYPE, SearchCountryActivity.TAG_COUNTRY);
        startActivityForResult(intent, ACTION_FOR_COUNTRY);
    }

    @Override
    public void onFliterApplyClick() {
        filter_contactNumber = filterBottomDialogFragment.getInput_ContactNumber();
        filter_areaCode = filterBottomDialogFragment.getInput_AreaCode();
        getTwilioFilterNumber("GB",filter_areaCode,filter_contactNumber);
        filterBottomDialogFragment.dismiss();
    }

    @Override
    public void onFilterClearClick() {
        filter_contactNumber = "";
        filter_areaCode = "";
        getTwilioFilterNumber("",filter_areaCode,filter_contactNumber);
        filterBottomDialogFragment.dismiss();
    }

    private void getTwilioFilterNumber(String country_iso, String area_code, String inputNumber) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this, "Please wait");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);

        //RequestBody
        String body = "";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country", country_iso);
        jsonObject.addProperty("area_code", area_code);
        jsonObject.addProperty("filter", inputNumber);

        if (twilioNumbers_regular != null && twilioNumbers_regular.size() > 0) {

            jsonObject.addProperty("sample_number", twilioNumbers_regular.get(0).phone_number);
        } else if (twilioNumbers_premium != null && twilioNumbers_premium.size() > 0) {

            jsonObject.addProperty("sample_number", twilioNumbers_premium.get(0).phone_number);
        } else if (twilioNumbers_elite != null && twilioNumbers_elite.size() > 0) {

            jsonObject.addProperty("sample_number", twilioNumbers_elite.get(0).phone_number);
        }


        jsonObject.addProperty("package_id", packageModel.get("packageId"));
        body = "json=" + jsonObject.toString();
        Log.i("getTwilioNumber", userID + " " + body);

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                twilioNumbers_regular.clear();
                twilioNumbers_premium.clear();
                twilioNumbers_elite.clear();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if(number_message.getVisibility()==View.VISIBLE)
//                                        number_message.setVisibility(View.GONE);
//                                }
//                            });
                            final String country_name = jsonObject.getString("country_name");
                            JSONObject phone_number = jsonObject.getJSONObject("phone_number");

                            //Add Default Number

                            if (phone_number.has("default")) {
                                JSONArray jsonArray_default = phone_number.getJSONArray("default");
                                for (int k = 0; k < jsonArray_default.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_default.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    System.out.println("Default number " + object.getString("phone_number"));
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "Regular";
                                    twilioNumbers_regular.add(twilioNumber);
                                }
                            }

                            //Add premium Number
                            if (phone_number.has("premium")) {
                                JSONArray jsonArray_premium = phone_number.getJSONArray("premium");
                                for (int k = 0; k < jsonArray_premium.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_premium.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    System.out.println("premium number " + object.getString("phone_number"));
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "premium";
                                    twilioNumbers_premium.add(twilioNumber);
                                }
                            }

                            //Add elite Number
                            if (phone_number.has("elite")) {
                                JSONArray jsonArray_elite = phone_number.getJSONArray("elite");
                                for (int k = 0; k < jsonArray_elite.length(); k++) {
                                    TwilioNumber twilioNumber = new TwilioNumber();
                                    JSONObject object = jsonArray_elite.getJSONObject(k);
                                    twilioNumber.phone_number = object.getString("phone_number");
                                    System.out.println("elite number " + object.getString("phone_number"));
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type = "elite";
                                    twilioNumbers_elite.add(twilioNumber);
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    display_numbers(twilioNumbers_regular, twilioNumbers_premium, twilioNumbers_elite);
                                }
                            });


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    try {

                        String res = response.body().string();
                        //-----------
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }
}
