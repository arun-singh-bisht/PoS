package com.posfone.promote.posfone.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.ChooseNumberActivity;
import com.posfone.promote.posfone.PackageActivity;
import com.posfone.promote.posfone.PreSignInActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.database.DAO;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.PackageModel;
import com.posfone.promote.posfone.model.TwilioNumber;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class NumberFragment extends BaseFragment implements AdapterView.OnItemClickListener, Callback{

    String fragmentName;
    private View view;

    private  String TAB_NAME ="";
    public static String TAB_COUNTRY ="tab_country";
    public static String TAB_AREA ="tab_area";
    public static String TAB_TYPE ="tab_type";


    private List<CountryModel> countryModelList;
    private List<TwilioNumber> twilioNumberList;
    private GenericListAdapter genericListAdapter = null;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("PaymentFragment",fragmentName+"Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("PaymentFragment",fragmentName+"Fragment onCreateView");

        view = inflater.inflate(R.layout.fragment_choose_number, container, false);

        initViews();

        return view;
    }

    private void initViews()
    {
        EditText ed_search =  view.findViewById(R.id.ed_search);
        if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
        {
            ed_search.setHint("Search country");
            loadData(null);
        }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
        {
            ed_search.setHint("Search type");
        }

        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
                {
                    loadContryList(s.toString());
                }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
                {
                    //ed_search.setHint("Search type");

                }

            }
        });

    }

    public void loadData(final CountryModel selectedCountry)
    {
        if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
        {
            countryModelList = DAO.getAllCountry();

            if(countryModelList==null || countryModelList.size()==0)
            {
                //Call Country API
                getCountryList();
            }else
            {
                //Load saved Country List
                loadContryList(null);
            }

        }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
        {
            //Reset ListView
            ListView listView =  view.findViewById(R.id.list_payment);
            listView.setAdapter(null);

            //Load wilio Number for selected country
            if(selectedCountry!=null)
            {
                getTwilioNumber(selectedCountry);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
        {
            ((ChooseNumberActivity)getActivity()).scrollToNextTab(countryModelList.get(i));

        }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
        {

        }
    }

    public void setTabName(String tab_name)
    {
        TAB_NAME = tab_name;
    }


    private void getCountryList()
    {

        //Show loading dialog
        final AlertDialog progressDialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setCancelable(false)
                .setMessage("Loading country data")
                .build();
        progressDialog.show();


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("x-api-key",ApiClient.X_API_KEY);
        Call call = RESTClient.call_GET(RESTClient.COUNTRY, hashMap, new okhttp3.Callback() {

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if(progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response)  {

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("countries");
                            if(jsonArray!=null && jsonArray.length()>0)
                            {
                                for(int i=0;i<jsonArray.length();i++ )
                                {

                                    final CountryModel countryModel = new CountryModel();

                                    JSONObject country = jsonArray.getJSONObject(i);
                                    countryModel.localId = country.getString("id");
                                    countryModel.name =  country.getString("name");
                                    countryModel.iso =  country.getString("iso");
                                    countryModel.phonecode =  country.getString("phonecode");
                                    countryModel.flag = country.getString("flag");
                                    countryModel.save();

                                   /* getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            PicasoImageUtil.saveImage(getActivity(),countryModel.getFlag(),null);
                                        }
                                    });*/
                                }
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadContryList(null);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();

                    }
                } else {
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void loadContryList(String containsText)
    {

        if(containsText==null)
            countryModelList = DAO.getAllCountry();
        else
            countryModelList = DAO.getAllCountry(containsText);

        if(countryModelList==null || countryModelList.size()==0)
            return;

        genericListAdapter = new GenericListAdapter(getActivity(),countryModelList.size(),R.layout.number_fragment_country_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                ImageView img_flag =  view.findViewById(R.id.img_flag);


                CountryModel countryModel = countryModelList.get(position);
                txt_country_name.setText(countryModel.getName());
                txt_country_code.setText(countryModel.getPhonecode());
                Picasso.with(getActivity()).load(countryModel.getFlag()).into(img_flag);

                return view;
            }
        };

        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
        listView.setAdapter(genericListAdapter);

    }

    private void getTwilioNumber(final CountryModel selectedCountry)
    {

            ListView listView =  view.findViewById(R.id.list_payment);
            listView.setAdapter(null);

                //Show loading dialog
                final AlertDialog progressDialog = new SpotsDialog.Builder()
                        .setContext(getActivity())
                        .setCancelable(false)
                        .setMessage("Please wait")
                        .build();
                progressDialog.show();

                //Header
                HashMap<String,String> header = new HashMap<>();
                header.put("x-api-key", ApiClient.X_API_KEY);
                //RequestBody
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("country", selectedCountry.getIso());
                jsonObject.addProperty("area_code",selectedCountry.getPhonecode());
                String body = "json="+jsonObject.toString();

                Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER, header, body, new okhttp3.Callback() {
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


                                    JSONObject phone_number = jsonObject.getJSONObject("phone_number");
                                    twilioNumberList = new ArrayList<>();

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
                                            twilioNumber.type ="default";
                                            twilioNumberList.add(twilioNumber);
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
                                            twilioNumberList.add(twilioNumber);
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
                                            twilioNumberList.add(twilioNumber);
                                        }
                                    }



                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadTwilioNumberList(null);
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

    private void loadTwilioNumberList(String searchedNumber)
    {
        if(twilioNumberList==null || twilioNumberList.size()==0)
            return;

        genericListAdapter = new GenericListAdapter(getActivity(),twilioNumberList.size(),R.layout.number_fragment_type_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_number =  view.findViewById(R.id.txt_number);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                TextView txt_number_type =  view.findViewById(R.id.txt_number_type);


                final TwilioNumber twilioNumber = twilioNumberList.get(position);
                txt_number.setText(twilioNumber.phone_number);
                txt_number_type.setText(twilioNumber.type.toUpperCase());


                txt_country_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String selectedTwilioNumber = twilioNumber.phone_number;
                        String selectedTwilioNumberType = twilioNumber.type;

                        try {
                            selectTwilioNumber(selectedTwilioNumberType,selectedTwilioNumber);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                       /* PackageModel selectedPackage = ((ChooseNumberActivity) getActivity()).getSelectedPackage();
                        selectedPackage.selectedTwillioNumber = twilioNumber.phone_number;

                        Intent intent = new Intent(getActivity(), PackageActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("SelectedPackage",selectedPackage);
                        intent.putExtras(bundle);
                        startActivity(intent);*/
                    }
                });

                return view;
            }
        };

        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
        listView.setAdapter(genericListAdapter);
    }

    private void selectTwilioNumber(String numberType,String number) throws JSONException {

        //Show loading dialog
        GeneralUtil.showProgressDialog(getActivity(),null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
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

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //loadTwilioNumberList(null);
                                    Intent intent = new Intent(getActivity(), PackageActivity.class);
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

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
