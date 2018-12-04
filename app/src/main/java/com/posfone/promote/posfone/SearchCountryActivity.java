package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.ImageUtil;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.database.DAO;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.StateModel;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class SearchCountryActivity extends AppCompatActivity implements View.OnClickListener {

    public static String TAG_TYPE = "type";
    public static String TAG_COUNTRY = "country";
    private AlertDialog progressDialog;

    private String selectedCountryName;
    private String selectedCountryFlag;
    private String selectedCountryPhoneCode;

    private List<StateModel> stateModelsList = new ArrayList<>();
    String type;
    private okhttp3.Call call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);

        initViews();


        if(type.equalsIgnoreCase(TAG_COUNTRY)) {
            try {
                if(DAO.getAllCountry().size()==0)
                    getCountryList();
                else
                    loadContryList(null);

            }catch (Exception e)
            {
                getCountryList();
            }
        }
        else {
            getStateList();
        }
    }

    private void initViews()
    {
        type = getIntent().getStringExtra(TAG_TYPE);
        selectedCountryFlag = getIntent().getStringExtra("selectedCountryFlag");
        selectedCountryPhoneCode = getIntent().getStringExtra("selectedCountryPhoneCode");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);


        TextView txt_title = findViewById(R.id.txt_title);
        EditText ed_search = findViewById(R.id.ed_search);

        if(type.equalsIgnoreCase(TAG_COUNTRY)) {
            txt_title.setText("Select Country");
            ed_search.setHint("Search country");

        }
        else {
            txt_title.setText(type);
            selectedCountryName = type;
            ed_search.setHint("Search state");
        }

        //Set TextChagne Listener
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String inputText = editable.toString();
                if(type.equalsIgnoreCase(TAG_COUNTRY)) {
                    loadContryList(inputText);
                }
                else {
                    List<StateModel> filterdList = StateModel.getFilterdList(stateModelsList, inputText);
                    loadStateList(filterdList);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
        }
    }




    private void getCountryList()
    {

        //Show loading dialog
         progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                 .setMessage("Loading country data")
                .build();
        progressDialog.show();


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("x-api-key",ApiClient.X_API_KEY);
        call = RESTClient.call_GET(RESTClient.COUNTRY, hashMap, new okhttp3.Callback() {

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

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ImageUtil.saveImage(SearchCountryActivity.this,countryModel.getFlag(),null);
                                        }
                                    });


                                }
                            }
                        }


                        runOnUiThread(new Runnable() {
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
        final List<CountryModel> countryList ;
        if(containsText==null)
            countryList = DAO.getAllCountry();
        else
            countryList = DAO.getAllCountry(containsText);


        GenericListAdapter genericListAdapter = new GenericListAdapter(SearchCountryActivity.this,countryList.size(),R.layout.number_fragment_country_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                ImageView img_flag =  view.findViewById(R.id.img_flag);

                CountryModel countryModel = countryList.get(position);
                txt_country_name.setText(countryModel.getName());
                txt_country_code.setText(countryModel.getPhonecode());

                Picasso.with(SearchCountryActivity.this).load(countryModel.getFlag()).into(img_flag);
                //ImageUtil.loadImageInImageView(SearchCountryActivity.this,countryModel.getFlag(),img_flag);

                return view;
            }
        };

        ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                CountryModel countryModel = countryList.get(position);
                String selectedCountryName = countryModel.getName();

                Intent intent = new Intent();
                intent.putExtra("result",selectedCountryName);
                intent.putExtra("selectedCountryFlag",countryModel.getFlag());
                intent.putExtra("selectedCountryPhoneCode",countryModel.getLocalId());
                intent.putExtra("selectedCountryCode",countryModel.getPhonecode());
                intent.putExtra("selectedCountryIso",countryModel.getIso());
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        listView.setAdapter(genericListAdapter);
    }

    private void getStateList()
    {

        //Show loading dialog
        progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Loading state data")
                .build();
        progressDialog.show();

        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("country",selectedCountryPhoneCode);
        String body = "json="+jsonObject.toString();

        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key",ApiClient.X_API_KEY);

        call = RESTClient.call_POST(RESTClient.STATE, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if(progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        stateModelsList.clear();
                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("statelist");
                            if(jsonArray!=null && jsonArray.length()>0)
                            {
                                for(int i=0;i<jsonArray.length();i++ )
                                {

                                    final StateModel stateModel = new StateModel();

                                    JSONObject state = jsonArray.getJSONObject(i);
                                    stateModel.localId = state.getString("id");
                                    stateModel.country_id = state.getString("country_id");
                                    stateModel.name = state.getString("name");
                                    stateModel.geo_lat = state.getString("geo_lat");
                                    stateModel.geo_lng = state.getString("geo_lng");
                                    stateModelsList.add(stateModel);
                                }
                            }
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadStateList(stateModelsList);
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


    private void loadStateList(final List<StateModel> stateModels)
    {
        GenericListAdapter genericListAdapter = new GenericListAdapter(SearchCountryActivity.this,stateModels.size(),R.layout.number_fragment_country_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                txt_country_code.setVisibility(View.GONE);

                ImageView img_flag =  view.findViewById(R.id.img_flag);

                StateModel stateModel = stateModels.get(position);
                txt_country_name.setText(stateModel.getName());
                ImageUtil.loadImageInImageView(SearchCountryActivity.this,selectedCountryFlag,img_flag);

                return view;
            }
        };

        ListView listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                StateModel stateModel = stateModels.get(position);
                String selectedStateName = stateModel.getName();

                Intent intent = new Intent();
                intent.putExtra("result",selectedStateName);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        listView.setAdapter(genericListAdapter);

    }

}
