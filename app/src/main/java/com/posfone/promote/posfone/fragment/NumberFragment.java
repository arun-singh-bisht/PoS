package com.posfone.promote.posfone.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.ChooseNumberActivity;
import com.posfone.promote.posfone.ChoosePlanActivity;
import com.posfone.promote.posfone.MainActivity;
import com.posfone.promote.posfone.PackageActivity;
import com.posfone.promote.posfone.PackageDetailActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.SignInActivity;
import com.posfone.promote.posfone.SignUpActivity;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.adapters.GenericListAdapter;
import com.posfone.promote.posfone.database.DAO;
import com.posfone.promote.posfone.model.BaseModel;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.TwilioNumber;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class NumberFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    String fragmentName;
    private View view;

    private  String TAB_NAME ="";
    public static String TAB_COUNTRY ="tab_country";
    public static String TAB_AREA ="tab_area";
    public static String TAB_TYPE ="tab_type";


    private List<CountryModel> countryModelList;
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
    }

    public void loadData(final CountryModel selectedCountry)
    {
        if(TAB_NAME.equalsIgnoreCase(TAB_COUNTRY))
        {
            countryModelList = DAO.getAllCountry();

            genericListAdapter = new GenericListAdapter(getActivity(),countryModelList.size(),R.layout.number_fragment_country_row){

                @Override
                public View initGenericView(View view, int position) {

                    TextView txt_country_name =  view.findViewById(R.id.txt_country_name);
                    TextView txt_country_code =  view.findViewById(R.id.txt_country_code);

                    CountryModel countryModel = countryModelList.get(position);
                    txt_country_name.setText(countryModel.getName());
                    txt_country_code.setText(countryModel.getPhonecode());

                    return view;
                }
            };

            ListView listView =  view.findViewById(R.id.list_payment);
            listView.setOnItemClickListener(this);
            listView.setAdapter(genericListAdapter);

        }else if(TAB_NAME.equalsIgnoreCase(TAB_TYPE))
        {

            ListView listView =  view.findViewById(R.id.list_payment);
            listView.setAdapter(null);

            if(selectedCountry!=null)
            {
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
                                    JSONArray jsonArray_premium = phone_number.getJSONArray("premium");
                                    JSONArray jsonArray_default = phone_number.getJSONArray("default");
                                    JSONArray jsonArray_elite = phone_number.getJSONArray("elite");

                                    final List<TwilioNumber> twilioNumberList = new ArrayList<>();

                                    //Add Default Number
                                    for(int k = 0;k<jsonArray_default.length();k++)
                                    {
                                        TwilioNumber twilioNumber = new TwilioNumber();
                                        JSONObject object =  jsonArray_default.getJSONObject(k);
                                        twilioNumber.phone_number = object.getString("phone_number");
                                        twilioNumber.friendly_number = object.getString("friendly_number");
                                        twilioNumber.voice = object.getBoolean("voice");
                                        twilioNumber.SMS = object.getBoolean("SMS");
                                        twilioNumber.MMS = object.getBoolean("MMS");
                                        twilioNumber.type ="Default";
                                        twilioNumberList.add(twilioNumber);
                                    }
                                    //Add premium Number
                                    for(int k = 0;k<jsonArray_premium.length();k++)
                                    {
                                        TwilioNumber twilioNumber = new TwilioNumber();
                                        JSONObject object =  jsonArray_premium.getJSONObject(k);
                                        twilioNumber.phone_number = object.getString("phone_number");
                                        twilioNumber.friendly_number = object.getString("friendly_number");
                                        twilioNumber.voice = object.getBoolean("voice");
                                        twilioNumber.SMS = object.getBoolean("SMS");
                                        twilioNumber.MMS = object.getBoolean("MMS");
                                        twilioNumber.type ="Premium";
                                        twilioNumberList.add(twilioNumber);
                                    }
                                    //Add elite Number
                                    for(int k = 0;k<jsonArray_elite.length();k++)
                                    {
                                        TwilioNumber twilioNumber = new TwilioNumber();
                                        JSONObject object =  jsonArray_elite.getJSONObject(k);
                                        twilioNumber.phone_number = object.getString("phone_number");
                                        twilioNumber.friendly_number = object.getString("friendly_number");
                                        twilioNumber.voice = object.getBoolean("voice");
                                        twilioNumber.SMS = object.getBoolean("SMS");
                                        twilioNumber.MMS = object.getBoolean("MMS");
                                        twilioNumber.type ="Elite";
                                        twilioNumberList.add(twilioNumber);
                                    }


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadTwilioNumberList(twilioNumberList);
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
    }

    private void loadTwilioNumberList(final List<TwilioNumber> twilioNumberList)
    {
        genericListAdapter = new GenericListAdapter(getActivity(),twilioNumberList.size(),R.layout.number_fragment_type_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_number =  view.findViewById(R.id.txt_number);
                TextView txt_country_code =  view.findViewById(R.id.txt_country_code);
                TextView txt_number_type =  view.findViewById(R.id.txt_number_type);


                TwilioNumber twilioNumber = twilioNumberList.get(position);
                txt_number.setText(twilioNumber.phone_number);
                txt_number_type.setText(twilioNumber.type);


                txt_country_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getActivity(),PackageActivity.class));
                    }
                });

                return view;
            }
        };

        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
        listView.setAdapter(genericListAdapter);
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

}
