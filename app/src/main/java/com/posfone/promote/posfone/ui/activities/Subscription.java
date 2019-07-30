package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.models.SubscriptionModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.adapters.PaymentListAdapter;
import com.posfone.promote.posfone.ui.fragments.BaseFragment;
import com.posfone.promote.posfone.ui.fragments.SubscriptionListAdapter;

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

public class Subscription extends BaseFragment implements AdapterView.OnItemClickListener{
    //326
    String fragmentName;
    private View view;
    SubscriptionListAdapter subscriptionListAdapter;
    List<SubscriptionModel> subscriptionModels;
    @BindView(R.id.label_package)
    TextView package_name;
    @BindView(R.id.btn_active_plan)
    TextView status_btn;
    @BindView(R.id.package_price)
    TextView package_price;
    @BindView(R.id.btn_stop_subscription)
    TextView stop_subscription;
    @BindView(R.id.btn_upgrade_payment_plan)
    TextView btn_upgrade_payment_plan;
    String subscription_id;
    boolean subscription_status;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Log.i("SubscriptionFragment",fragmentName+"Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("SubscriptionFragment",fragmentName+"Fragment onCreateView");
        view = inflater.inflate(R.layout.activity_subscription, container, false);
        ButterKnife.bind(this,view);
        initViews();
        return view;
    }

    private void initViews()
    {
        subscriptionModels = new ArrayList<>();
        // paymentListAdapter = new SubscriptionListAdapter(getActivity(),null);
        ListView listView =  (ListView) view.findViewById(R.id.list_item);
        listView.setOnItemClickListener(this);
        get_subscription_list();

    }

    public void update_list(List< SubscriptionModel> subscriptionModelList) {
        subscriptionListAdapter = new SubscriptionListAdapter(getActivity(),subscriptionModelList);
        ListView listView =  (ListView) view.findViewById(R.id.list_item);
        listView.setAdapter(subscriptionListAdapter);
    }

    @Override
    public void onResume() {
        Log.i("PaymentFragment",fragmentName+"Fragment onResume");
        super.onResume();

        setTitle("Subscription List");
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

   private void get_subscription_list() {
       //Show loading dialog
       GeneralUtil.showProgressDialog(getActivity(), "Loading Data...");
       SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
       String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);


       //Header
       HashMap<String, String> header = new HashMap<>();
       header.put("x-api-key", ApiClient.X_API_KEY);
       header.put("userid", "326");
       //RequestBody
       Log.e("param",ApiClient.X_API_KEY+" -> "+userID);
       Call call = RESTClient.call_POST(RESTClient.SUBSCRIPTION, header, "", new okhttp3.Callback() {
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

                           JSONArray subscription_list = jsonObject.getJSONArray("subscription_list");
                           JSONObject current_subscription = jsonObject.getJSONObject("current_subscription");
                           Gson gson = new Gson();
                           for(int i=0; i<subscription_list.length();i++){
                               SubscriptionModel object = gson.fromJson(subscription_list.get(i).toString(), SubscriptionModel.class);
                               subscriptionModels.add(object);
                           }
                           load_data(true, current_subscription);
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

   @OnClick(R.id.btn_stop_subscription)
   public void Stop_subscription() {
       //Show loading dialog
       Intent intent = new Intent(getActivity(),RenewSubscription.class);
       intent.putExtra("package_id", "141");
       intent.putExtra("package_name",package_name.getText());
       getActivity().startActivity(intent);
       return;

       /*String url;
       if (subscription_status)
       url = RESTClient.STOP_SUBSCRIPTION;
       else
       url = RESTClient.RENEW_SUBSCRIPTION;
           GeneralUtil.showProgressDialog(getActivity(), "Please Wait...");
       SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(getActivity());
       String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);


       //Header
       HashMap<String, String> header = new HashMap<>();
       header.put("x-api-key", ApiClient.X_API_KEY);
       //RequestBody
       JSONObject object = new JSONObject();
       try {
           object.put("user_id",userID);
           if(subscription_status)
           object.put("sub_id",subscription_id);
       } catch (JSONException e) {
           e.printStackTrace();
       }

       String body = "json="+object.toString();
       Log.e("param",ApiClient.X_API_KEY+" -> "+userID);
       Call call = RESTClient.call_POST(url, header, body, new okhttp3.Callback() {
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

                           String message = jsonObject.getString("message");
                           print_data(message);
                           get_subscription_list();
                           Log.e("succesfull_message",message);
                       }

                   } catch (Exception e) {
                       e.printStackTrace();
                   } finally {
                       //-----
                   }
               } else {
                   String res = null;
                   try {
                       res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        print_data(jsonObject.getString("message"));
                   } catch (IOException e) {
                       e.printStackTrace();
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

                   // Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_SHORT).show();
                   //-----------
               }
           }
       });*/
   }

   public void print_data(final String message) {
       getActivity().runOnUiThread(new Runnable() {
           @Override
           public void run() {

               CustomAlertDialog.showDialogSingleButton(getActivity(),message, new CustomAlertDialog.I_CustomAlertDialog() {
                   @Override
                   public void onPositiveClick() {

                   }
                   @Override
                   public void onNegativeClick(){

                   }
               });
           }
       });
   }


   public void load_data(final boolean status, final JSONObject jsonObject) {
       getActivity().runOnUiThread(new Runnable() {
           @Override
           public void run() {
               update_list(subscriptionModels);
               if (status) {
                   try {
                       String isactive =jsonObject.getString("subscription_status");
                       if (isactive.equals("Inactive")){
                           status_btn.setBackground(getResources().getDrawable(R.drawable.round_inactive_color));
                           stop_subscription.setBackground(getResources().getDrawable(R.drawable.round_inactive_color));
                           stop_subscription.setText("Renew Subscription");
                           subscription_status = false;
                       } else {
                           subscription_status= true;
                       }
                       package_name.setText(jsonObject.getString("package_name"));
                       status_btn.setText(isactive);
                       subscription_id = jsonObject.getString("subscription_id");
                       Log.e("id is",subscription_id);
                       package_price.setText("£ "+jsonObject.getString("subscription_amount"));
                   }catch (Exception e){
                       e.printStackTrace();
                   }
               } else {
                  package_name.setText("No Active Subscription");
                   package_price.setText("£ 00");
                   status_btn.setVisibility(View.INVISIBLE);
                   stop_subscription.setVisibility(View.INVISIBLE);
                   btn_upgrade_payment_plan.setVisibility(View.INVISIBLE);
               }
           }
       });
   }
}
