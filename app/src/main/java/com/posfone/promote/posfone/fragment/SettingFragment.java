package com.posfone.promote.posfone.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.ManageNumberActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.SearchCountryActivity;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.CustomSelectorDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.Utils.TitilliumWebTextView;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class SettingFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    private int ACTION_FOR_COUNTRY_OUTGOING_CALL = 1001;
    private int ACTION_FOR_COUNTRY_INCOMING_CALL = 1002;

    String fragmentName;
    String voicePeference;
    String posfone_number="Your Account will show calls coming in from and going out from the POSfone number";
    String my_number="Your Account will show calls coming in from and going out from the your mobile/telephone number";
    String callerIdPreference;
    String numberFoMakingCall_country;
    String numberFoMakingCall_code;
    static String numberFoMakingCall_number;
    String numberForReceivingCall_country;
    String numberForReceivingCall_code;
    static String numberForReceivingCall_number;
    String pay729number;


    @BindView(R.id.pay_number)
    TextView pay_number;
    @BindView(R.id.call_preference_text)
    TitilliumWebTextView preference_text;
    @BindView(R.id.tv_number_for_recv_call_country)
    TextView tv_number_for_recv_call_country;
    @BindView(R.id.tv_number_for_recv_call)
    EditText tv_number_for_recv_call;
    @BindView(R.id.tv_number_for_making_call_country)
    TextView tv_number_for_making_call_country;
    @BindView(R.id.tv_number_for_making_call)
    EditText tv_number_for_making_call;

    @BindView(R.id.txt_voice_preference)
    TextView txt_voice_preference;
    @BindView(R.id.txt_caller_id_preference)
    TextView txt_caller_id_preference;


    private View view;
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

        view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this,view);
        getCallProfle();
        return view;
    }



    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();

        setTitle("Telephone Number");
    }


    private void initViews()
    {
        pay_number.setText(pay729number+"");
        tv_number_for_recv_call_country.setText(numberForReceivingCall_country+"");
        tv_number_for_recv_call.setText(numberForReceivingCall_number);
        tv_number_for_making_call_country.setText(numberFoMakingCall_country+"");
        tv_number_for_making_call.setText(numberFoMakingCall_number);

        if(voicePeference.equalsIgnoreCase("male"))
            txt_voice_preference.setText("Male");
        else if(voicePeference.equalsIgnoreCase("female"))
            txt_voice_preference.setText("Female");

        if(callerIdPreference.equalsIgnoreCase("my_pay729_number")){
            txt_caller_id_preference.setText("POSfone Number");
        preference_text.setText(posfone_number);}
        else if(callerIdPreference.equalsIgnoreCase("customer_number")){
            txt_caller_id_preference.setText("My Number");
        preference_text.setText(my_number);}

    }


    @OnClick(R.id.txt_voice_preference)
    public void onVoicePreferenceClick()
    {
        //VoicePreference Option Generate
        CustomSelectorDialog.Item item_male = new CustomSelectorDialog.Item();
        item_male.itemName ="Male";
        if(voicePeference.equalsIgnoreCase(item_male.itemName))
            item_male.isSelected = true;
        else
            item_male.isSelected = false;

        CustomSelectorDialog.Item item_female = new CustomSelectorDialog.Item();
        item_female.itemName ="Female";
        if(voicePeference.equalsIgnoreCase(item_female.itemName))
            item_female.isSelected = true;
        else
            item_female.isSelected = false;



        final List<CustomSelectorDialog.Item> itemList = new ArrayList<>();
        itemList.add(item_male);
        itemList.add(item_female);

        CustomSelectorDialog.showDialog(getActivity(), "Select Voice", itemList, new CustomSelectorDialog.I_CustomSelectorDialog() {
            @Override
            public void onItemSelected(CustomSelectorDialog.Item item) {
                txt_voice_preference.setText(item.itemName);
                voicePeference = item.itemName;
            }
        });
    }

    @OnClick(R.id.txt_caller_id_preference)
    public void onCallerIdPreferenceClick()
    {
        //VoicePreference Option Generate
        CustomSelectorDialog.Item item_1 = new CustomSelectorDialog.Item();
        item_1.itemName ="POSfone Number";
        item_1.itemName_alias = "my_pay729_number";
        if(callerIdPreference.equalsIgnoreCase(item_1.itemName_alias))
            item_1.isSelected = true;
        else
            item_1.isSelected = false;

        CustomSelectorDialog.Item item_2 = new CustomSelectorDialog.Item();
        item_2.itemName ="My Number";
        item_2.itemName_alias = "customer_number";
        if(callerIdPreference.equalsIgnoreCase(item_2.itemName_alias))
            item_2.isSelected = true;
        else
            item_2.isSelected = false;

        final List<CustomSelectorDialog.Item> itemList = new ArrayList<>();
        itemList.add(item_1);
        itemList.add(item_2);

        CustomSelectorDialog.showDialog(getActivity(), "Select Caller ID", itemList, new CustomSelectorDialog.I_CustomSelectorDialog() {
            @Override
            public void onItemSelected(CustomSelectorDialog.Item item) {
                txt_caller_id_preference.setText(item.itemName);
                callerIdPreference = item.itemName_alias;
                if(callerIdPreference.equals("customer_number"))
                    preference_text.setText(my_number);
                else
                    preference_text.setText(my_number);
            }
        });

    }

    @OnClick(R.id.tv_number_for_recv_call_country)
    public void onClick_IncomingNumber_country()
    {
        Intent intent = new Intent(getActivity(),SearchCountryActivity.class);
        intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
        startActivityForResult(intent,ACTION_FOR_COUNTRY_INCOMING_CALL);
    }

    @OnClick(R.id.tv_number_for_making_call_country)
    public void onClick_OutgoingNumber_country()
    {
        Intent intent = new Intent(getActivity(),SearchCountryActivity.class);
        intent.putExtra(SearchCountryActivity.TAG_TYPE,SearchCountryActivity.TAG_COUNTRY);
        startActivityForResult(intent,ACTION_FOR_COUNTRY_OUTGOING_CALL);
    }



    @OnClick(R.id.btn_save)
    public void onSaveClick()
    {
        numberForReceivingCall_number = tv_number_for_recv_call.getText().toString();
        numberFoMakingCall_number  = tv_number_for_making_call.getText().toString();

        if(numberForReceivingCall_number==null || numberForReceivingCall_number.isEmpty()) {
            GeneralUtil.showToast(getActivity(), "Please enter number on which you want to receive call.");
            return;
        }else if(numberFoMakingCall_number == null || numberFoMakingCall_number.isEmpty())
        {
            GeneralUtil.showToast(getActivity(), "Please enter number from which you want to make call.");
            return;
        }

        saveSetting();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == getActivity().RESULT_OK)
        {
            String country_name = data.getStringExtra("result");
            String selectedCountryCode = data.getStringExtra("selectedCountryCode");
            if(requestCode == ACTION_FOR_COUNTRY_INCOMING_CALL)
            {
                numberForReceivingCall_code = "+"+selectedCountryCode;
                tv_number_for_recv_call_country.setText(country_name);

            }else if(requestCode == ACTION_FOR_COUNTRY_OUTGOING_CALL)
            {
                numberFoMakingCall_code = "+"+selectedCountryCode;
                tv_number_for_making_call_country.setText(country_name);
            }
            Log.i("onActivityResult",selectedCountryCode);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }

    private void getCallProfle() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(getActivity(),null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        String token = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_TOKEN);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        header.put("token", token);
        //RequestBody

        Call call = RESTClient.call_GET(RESTClient.MANAGE_NUMBER, header,  new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @ Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONObject mypreference =  jsonObject.getJSONObject("mypreference");
                            voicePeference =  mypreference.getString("voice_preference").trim();
                            callerIdPreference = mypreference.getString("caller_id_preference").trim();

                            JSONObject in1_user_number =  mypreference.getJSONObject("in1_user_number");
                            numberForReceivingCall_country = in1_user_number.getString("country");
                            numberForReceivingCall_code = "+"+in1_user_number.getString("code");
                            numberForReceivingCall_number = in1_user_number.getString("number");


                            JSONObject out1_user_number =  mypreference.getJSONObject("out1_user_number");
                            numberFoMakingCall_country = out1_user_number.getString("country");
                            numberFoMakingCall_code = "+"+out1_user_number.getString("code");
                            numberFoMakingCall_number = out1_user_number.getString("number");

                            pay729number = "+"+ jsonObject.getString("pay729numbers");
/*
                            JSONArray pay729numbers = jsonObject.getJSONArray("pay729numbers");
                            if(pay729numbers.length()>0)
                            {
                                JSONObject jsonObject1 = pay729numbers.getJSONObject(0);
                                pay729number =  jsonObject1.getString("phone_number").trim();
                            }*/

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initViews();
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

    private void saveSetting() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(getActivity(),null);

        final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("in1c",numberForReceivingCall_code);
        jsonObject.addProperty("in1",numberForReceivingCall_number);
        jsonObject.addProperty("out1c",numberFoMakingCall_code);
        jsonObject.addProperty("out1",numberFoMakingCall_number);
        jsonObject.addProperty("voice_preference",voicePeference.toLowerCase());
        jsonObject.addProperty("caller_id_preference",callerIdPreference);

        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.MANAGE_NUMBERS, header, body, new okhttp3.Callback() {
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
                        String message = jsonObject.getString("message");
                       String preference= txt_caller_id_preference.getText().toString();
                        String number= tv_number_for_making_call.getText().toString();


                        SharedPreferenceHandler sharedPreferenceHandler=new SharedPreferenceHandler(getActivity());
                        preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_CALLER_ID,preference);
//                        Toast.makeText(getActivity(),preference,Toast.LENGTH_SHORT).show();
                        //System.out.println(preference);


                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomAlertDialog.showDialogSingleButton(getActivity(), "New settings saved.", new CustomAlertDialog.I_CustomAlertDialog() {
                                        @Override
                                        public void onPositiveClick() {
                                            //Redirect user to PreSignInActivity
                                        }

                                        @Override
                                        public void onNegativeClick() {

                                        }
                                    });
                                }
                            });

                        }else
                        {
                            GeneralUtil.showToast(getActivity(),message);
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
