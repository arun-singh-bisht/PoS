package com.posfone.promote.posfone.ui.activities;

import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.models.Charge;
import com.posfone.promote.posfone.data.local.models.SubscriptionModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.models.RenewSubscriptionModel;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;

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
import okhttp3.Response;

import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.RIGHT_OF;

public class RenewSubscription extends AppCompatActivity {
    String userid;
    String package_id;
    String package_name_returned;
    @BindView(R.id.package_name)
    TextView package_name;
    @BindView(R.id.due_sub_date)
    TextView due_sub_date;
    @BindView(R.id.total_chargeable_amount)
    TextView total_chargeable_amount;
    @BindView(R.id.package_gateway)
    TextView package_gateway;
    @BindView(R.id.call_bundle)
    TextView call_bundle;
    @BindView(R.id.package_contract)
    TextView package_contract;
    @BindView(R.id.total_sub_due)
    TextView total_sub_due;
   // @BindView(R.id.subscription_charge)
   // TextView subscription_charge;
   // @BindView(R.id.gateway_charge)
   // TextView gateway_charge;
    @BindView(R.id.total_monthly_cost)
    TextView total_monthly_cost;
    @BindView(R.id.total_sub_cost_till_date)
    TextView total_sub_cost_till_date;
   // @BindView(R.id.user_license)
   // TextView user_license;
    @BindView(R.id.total_amount)
    TextView total;
    @BindView(R.id.vat)
    TextView vat;
    @BindView(R.id.img_right)
    ImageView img_right;
    @BindView(R.id.txt_title)
    TextView title;
    @BindView(R.id.charges_layout)
    LinearLayout charges_layout;
  //  @BindView(R.id.charges_relative_layout)
   // RelativeLayout charges_relative_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renew_subscription);
         package_id= getIntent().getStringExtra("package_id");
         package_name_returned = getIntent().getStringExtra("package_name");
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        userid= preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        ButterKnife.bind(this);
        title.setText("Renew Subscription");
        img_right.setVisibility(View.INVISIBLE);
        getSupportActionBar().hide();
        get_renew_details(userid,package_id);
    }

    @OnClick(R.id.img_left)
    public void back(){
        finish();
    }

    private void get_renew_details(String userid, String package_id) {
        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
       // header.put("userid", userID);
        //RequestBody
        //RequestBody
        JSONObject object = new JSONObject();
        try {
            object.put("user_id","326");
            object.put("package_id",package_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String body = "json="+object.toString();
        Log.e("param",package_id+" -> "+"326");
        Call call = RESTClient.call_POST(RESTClient.SUBSCRIPTION_INFO, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, Response response) {

                GeneralUtil.dismissProgressDialog();
                Log.e("code",String.valueOf( response.code()));
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        Log.i("onResponse", res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("success") && jsonObject.getString("success").equalsIgnoreCase("1")) {

                            JSONObject data = jsonObject.getJSONObject("data");
                            Gson gson = new Gson();
                            Log.i("onResponse", data.toString());
                            RenewSubscriptionModel object = gson.fromJson(data.toString(), RenewSubscriptionModel.class);
                            Log.e("model",object.getCharge().get(0).getName());
                            load_data(true, object);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    try {
                    String res = response.body().string();
                    Log.e("renew", res);
                    final JSONObject jsonObject = new JSONObject(res);
                        Toast.makeText(RenewSubscription.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    load_data(false, null);
                }catch (Exception e){
                    e.printStackTrace();
                }
                    // Toast.makeText(getActivity(), "No Data Available", Toast.LENGTH_SHORT).show();
                    //-----------
                }
            }
        });
    }

    private void load_data(final boolean status,final  RenewSubscriptionModel subscriptionModel){
      runOnUiThread(new Runnable() {
          @Override
          public void run() {
              if (status) {
                  set_charges(subscriptionModel.getCharge());
                  package_name.setText(package_name_returned);
                  due_sub_date.setText(subscriptionModel.getDue_sub_date());
                  total_chargeable_amount.setText("£ "+subscriptionModel.getTotal_chargeable_amount());
                  package_gateway.setText(subscriptionModel.getPackage_gateway());
                  call_bundle.setText(subscriptionModel.getCall_bundle());
                  package_contract.setText(subscriptionModel.getPackage_contract());
                  total_sub_due.setText(subscriptionModel.getTotal_sub_date());
                //  subscription_charge.setText("£ "+"0.00");
                //  gateway_charge.setText("£ "+"0.00");
                  total_monthly_cost.setText("£ "+subscriptionModel.getTotal_monthly_cost());
                  total_sub_cost_till_date.setText("£ "+subscriptionModel.getTotal_sub_cost_till_date());
                //  user_license.setText("£ "+"0.00");
                  total.setText("£ "+subscriptionModel.getTotal_chargeable_amount());
                  vat.setText("£ "+subscriptionModel.getVat());
              }
          }
      });

    }


    public void set_charges(ArrayList<Charge> charges) {
        Log.e("set_charges","setting....");
        for (int i=0;i<charges.size();i++) {
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            RelativeLayout.LayoutParams layoutParams_relative = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            TextView label = new TextView(this);
            label.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            label.setText(charges.get(i).getName());
            label.setTextSize(13);
            int id = View.generateViewId();
            label.setId(id);

            TextView label_tag = new TextView(this);
            label_tag.setLayoutParams(new RelativeLayout.LayoutParams(100,
                    40));
            label_tag.setText(charges.get(i).getType());
            label_tag.setBackground(ContextCompat.getDrawable(this, R.drawable.bdge));
            label_tag.setPadding(2,2,2,2);
            label_tag.setTextSize(10);
            label_tag.setTextColor(ContextCompat.getColor(this, R.color.color_search_bg_light));
            RelativeLayout.LayoutParams layoutParams_tag = (RelativeLayout.LayoutParams) label_tag.getLayoutParams();
            layoutParams_tag.addRule(RIGHT_OF, id);

            layoutParams_tag.setMarginStart(5);
            // label_tag.setLayoutParams(new RelativeLayout.LayoutParams( RelativeLayout.RIGHT_OF,id));

            TextView label_amount = new TextView(this);
            label_amount.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            label_amount.setTextSize(13);
            label_amount.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) label_amount.getLayoutParams();
            layoutParams.addRule(ALIGN_PARENT_RIGHT);
            label_amount.setText("£ "+charges.get(i).getAmount());
            relativeLayout.addView(label);
            relativeLayout.addView(label_tag);
            relativeLayout.addView(label_amount);
            layoutParams_relative.setMargins(0,40,0,0);
            charges_layout.addView(relativeLayout,i);
        }
    }
}
