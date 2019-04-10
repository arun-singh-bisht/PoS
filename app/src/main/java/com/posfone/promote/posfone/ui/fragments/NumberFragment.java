package com.posfone.promote.posfone.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.models.CountryModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.models.TwilioNumber;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.activities.ChooseNumberActivity;
import com.posfone.promote.posfone.ui.adapters.GenericListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class NumberFragment extends BaseFragment {

    String fragmentName;
    private View view;

    public NumberFragment numberFragment_type_regular;
    public NumberFragment numberFragment_type_premium;
    public NumberFragment numberFragment_type_elite;
    private final int ACTION_FOR_COUNTRY = 1001;
    private List<TwilioNumber> twilioNumberList;
    private GenericListAdapter genericListAdapter = null;
    ImageButton filter_button;
    LinearLayout linearLayout;
    LinearLayout number_message;
    EditText code;
    ImageView search;
    TextView search_error;

    //--------------------------
    List<TwilioNumber> twilioNumbers_regular = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_premium = new ArrayList<>();
    List<TwilioNumber> twilioNumbers_elite = new ArrayList<>();


    private ChooseNumberActivity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ChooseNumberActivity)activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("NumberFragment",fragmentName+" Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("NumberFragment",fragmentName+" Fragment onCreateView");

        view = inflater.inflate(R.layout.fragment_choose_number, container, false);

        number_message=view.findViewById(R.id.number_message);

        initViews();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("NumberFragment",fragmentName+" Fragment onResume");
        loadTwilioNumberList(twilioNumberList);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("NumberFragment",fragmentName+" Fragment onPause");
    }

    public void setFragmentName(String name)
    {
        this.fragmentName = name;
    }

    private void initViews()
    {
        filter_button=view.findViewById(R.id.country_code_button);
        linearLayout=view.findViewById(R.id.code_view);
        code=view.findViewById(R.id.country_code);
        search_error=view.findViewById(R.id.search_error);
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (linearLayout.getVisibility() == View.GONE){

                    linearLayout.setVisibility(View.VISIBLE);
                    Animation RightSwipe = AnimationUtils.loadAnimation(getActivity(), R.anim.left_slide);
                    linearLayout.startAnimation(RightSwipe);
            }
                else{
                    linearLayout.setVisibility(View.GONE);
                }
            }
        });
        final EditText ed_search =  view.findViewById(R.id.ed_search);
        ed_search.setHint("Search number");

        //----------------Search Filter
        search=view.findViewById(R.id.search_button);

        //search.setBackgroundResource(R.drawable.search_button);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.GONE);
                CountryModel countrysearchModel = new CountryModel();
                countrysearchModel.name= ChooseNumberActivity.country_name_search;
                countrysearchModel.iso=ChooseNumberActivity.country_iso_search;
                countrysearchModel.phonecode=ChooseNumberActivity.country_code_search;
                //System.out.println(ChooseNumberActivity.country_name_search+"---"+ChooseNumberActivity.country_iso_search+"---"+ChooseNumberActivity.country_code_search);
                if(ed_search.getText().toString().length()==1){
                   search_error.setVisibility(View.VISIBLE);
                }else{
                    search_error.setVisibility(View.GONE);
                getTwilioFilterNumber(countrysearchModel,ed_search.getText().toString());
                }
                /*if(new ChooseNumberActivity().isValid())
                    getTwilioFilterNumber(countrysearchModel,ed_search.getText().toString());
                else{
                    List<TwilioNumber> filterdList = TwilioNumber.getFilterdList(twilioNumberList, ed_search.getText().toString()+"");
                    loadTwilioNumberList(filterdList);}*/
            }
        });
    }

    public void setData(List<TwilioNumber> twilioNumberList)
    {
        this.twilioNumberList = twilioNumberList;
        loadTwilioNumberList(twilioNumberList);
    }

    private void loadTwilioNumberList(final List<TwilioNumber> list)
    {

        if(view==null)
            return;

        if(list==null ) {
            Log.i("loadTwilioNumberList:",list+"");
            return;
        }
        System.out.println("list size--------  "+list.size());

        genericListAdapter = new GenericListAdapter(getActivity(),list.size(),R.layout.number_fragment_type_row){

            @Override
            public View initGenericView(View view, int position) {

                TextView txt_number = view.findViewById(R.id.txt_number);
                TextView txt_country_code = view.findViewById(R.id.txt_country_code);
                //TextView txt_number_type =  view.findViewById(R.id.txt_number_type);

                 if(position<list.size()) {
                System.out.println("list size--------  " + list.size() + " -------- " + position);
                final TwilioNumber twilioNumber = list.get(position);

                txt_number.setText(twilioNumber.phone_number);
                //txt_number_type.setText(twilioNumber.type.toUpperCase());

                txt_country_code.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String selectedTwilioNumber = twilioNumber.phone_number;
                        String selectedTwilioNumberType = twilioNumber.type;

                        try {
                            activity.selectTwilioNumber(selectedTwilioNumber, selectedTwilioNumberType);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }

                return view;
            }
        };

        ListView listView =  view.findViewById(R.id.list_payment);
        listView.setAdapter(genericListAdapter);
    }



    //----------Testing Api

    //------------------------------
    private void getTwilioFilterNumber(final CountryModel selectedCountry,String filter)
    {

        //Show loading dialog
        GeneralUtil.showProgressDialog(getActivity(),"Please wait");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
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
            jsonObject.addProperty("area_code",code.getText().toString() );
            jsonObject.addProperty("filter",filter);
            body = "json=" + jsonObject.toString();
        }
        Log.i("getTwilioNumber",userID+" "+body);

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
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                             getActivity().runOnUiThread(new Runnable() {
                                 @Override
                                 public void run() {
                                     if(number_message.getVisibility()==View.VISIBLE)
                                         number_message.setVisibility(View.GONE);
                                 }
                             });
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
                                    System.out.println("Default number "+object.getString("phone_number"));
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
                                    System.out.println("premium number "+object.getString("phone_number"));
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
                                    System.out.println("elite number "+object.getString("phone_number"));
                                    twilioNumber.friendly_number = object.getString("friendly_number");
                                    twilioNumber.voice = object.getBoolean("voice");
                                    twilioNumber.SMS = object.getBoolean("SMS");
                                    twilioNumber.MMS = object.getBoolean("MMS");
                                    twilioNumber.type ="elite";
                                    twilioNumbers_elite.add(twilioNumber);
                                }
                            }


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ChooseNumberActivity chooseNumberActivity=new ChooseNumberActivity();
                                    TextView textView=getActivity().findViewById(R.id.txt_select_country);
                                    //textView.setText(country_name);
                                    chooseNumberActivity.display_numbers(twilioNumbers_regular,twilioNumbers_premium,twilioNumbers_elite);
                                    /*setData(twilioNumbers_regular);
                                    setData(twilioNumbers_premium);
                                    setData(twilioNumbers_elite);*/
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
                        Log.i("onResponse",res);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(number_message.getVisibility()==View.GONE)
                                    number_message.setVisibility(View.VISIBLE);
                            }
                        });

                        //-----------
                    }catch (Exception e){e.printStackTrace();}
                }

            }
        });

    }


}
