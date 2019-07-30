package com.posfone.promote.posfone.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.models.PaymentModel;
import com.posfone.promote.posfone.data.local.models.SubscriptionModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.adapters.PaymentListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class PaymentFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    String fragmentName;
    private View view;

    List<PaymentModel> paymentModels;
    PaymentListAdapter paymentListAdapter;
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

        view = inflater.inflate(R.layout.fragment_payment, container, false);

        initViews();

        return view;
    }

    @Override
    public void onStart() {
        Log.i("PaymentFragment",fragmentName+"Fragment onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();

        setTitle("Payment");
    }

    @Override
    public void onPause() {
        Log.i("PaymentFragment",fragmentName+"Fragment onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("PaymentFragment",fragmentName+"Fragment onStop");
        super.onStop();
    }


    @Override
    public void onDestroyView() {
        Log.i("PaymentFragment",fragmentName+"Fragment onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i("PaymentFragment",fragmentName+"Fragment onDestroy");
        super.onDestroy();
    }


    private void initViews()
    {
        paymentModels = new ArrayList<>();
        ListView listView =  (ListView) view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
       // listView.setAdapter(paymentListAdapter);
    }

    public void update_list(List< PaymentModel> paymentModels) {
        paymentListAdapter = new PaymentListAdapter(getActivity(),paymentModels);
        ListView listView =  (ListView) view.findViewById(R.id.list_item);
        listView.setAdapter(paymentListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        PaymentRecipetFragment paymentRecipetFragment = new PaymentRecipetFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,paymentRecipetFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void get_all_payment_details(){

        //Show loading dialog
        GeneralUtil.showProgressDialog(getActivity(), "Loading Data...");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);


        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        Log.e("param",ApiClient.X_API_KEY+" -> "+userID);
        Call call = RESTClient.call_POST(RESTClient.PAYMENT, header, "", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();
                Log.e("code",String.valueOf( response.code()));
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        Log.i("onResponse", res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            JSONArray payment_list = jsonObject.getJSONArray("payment");
                            Gson gson = new Gson();
                            for(int i=0; i<payment_list.length();i++){
                                PaymentModel object = gson.fromJson(payment_list.get(i).toString(), PaymentModel.class);
                                paymentModels.add(object);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    load_data(false, null);
                    // Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_SHORT).show();
                    //-----------
                }
            }
        });
    }


    public void load_data(final boolean status, final JSONObject jsonObject) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                update_list(paymentModels);
            }
        });
    }
}
