package com.posfone.promote.posfone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.posfone.promote.posfone.model.PackageModel;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;


public class ChoosePlanActivity extends AppCompatActivity implements View.OnClickListener {


    private List<PackageModel> packageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_plan);

        initViews();
        getPackages();
    }

    private void initViews()
    {
        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Choose Number");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setVisibility(View.GONE);
        //findViewById(R.id.img_left).setOnClickListener(this);
        //findViewById(R.id.get_free_number).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        /*switch (v.getId())
        {
            case R.id.get_free_number:{
                //startActivity(new Intent(ChoosePlanActivity.this,ChooseNumberActivity.class));
            }
            break;
        }*/
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ChoosePlanActivity.this,PreSignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private void getPackages()
    {
        //Show loading dialog
        final AlertDialog progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Please wait")
                .build();
        progressDialog.show();

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);


        Call call = RESTClient.call_POST(RESTClient.PACKAGES, header, "", new okhttp3.Callback() {
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

                            packageModelList = new ArrayList<>();

                            JSONArray jsonArray = jsonObject.getJSONArray("packages");
                            for(int i =0;i<jsonArray.length();i++)
                            {
                                PackageModel packageModel = new PackageModel();

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                packageModel.package_id = jsonObject1.getString("package_id");
                                packageModel.package_name = jsonObject1.getString("package_name");
                                packageModel.recurring_total = jsonObject1.getString("recurring_total");
                                packageModel.onetime_amount_flat = jsonObject1.getString("onetime_amount_flat");

                                JSONObject parameters =  jsonObject1.getJSONObject("parameters");
                                packageModel.parameters_Subscription_Charge = parameters.getString("Subscription Charge");

                                JSONObject gateway =  jsonObject1.getJSONObject("gateway");
                                packageModel.gateway_name = gateway.getString("name");
                                packageModel.gateway_transaction_fee_partner = gateway.getString("transaction_fee_partner");
                                packageModel.gateway_processing_fee_partner = gateway.getString("processing_fee_partner");

                                JSONObject recurring_amount_flat =  jsonObject1.getJSONObject("recurring_amount_flat");
                                packageModel.recurring_amount_flat_Subscription_Charge = recurring_amount_flat.getString("Subscription Charge");

                                packageModelList.add(packageModel);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadPakages();
                            }
                        });


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


    private void loadPakages()
    {
        if(packageModelList==null || packageModelList.size()==0)
            return;

        //get layout Height
       ScrollView scrollView = findViewById(R.id.parentlayout);
       int screenHeight = scrollView.getMeasuredHeight();

        int singleItemHeight =0;
        if(packageModelList.size()>2)
            singleItemHeight = screenHeight/3;
        else
            singleItemHeight = screenHeight/packageModelList.size();

        LinearLayout linearLayout = findViewById(R.id.parentlayout_inner);
       for(int i =0;i<packageModelList.size();i++)
       {
           final int position = i;
           // Changes the height and width to the specified *pixels*
           LinearLayout pakage_item = (LinearLayout) getLayoutInflater().inflate(R.layout.pakage_item, null);
           LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                   singleItemHeight);
           pakage_item.setLayoutParams(lp);
           View pakage_parent_layout  = pakage_item.findViewById(R.id.pakage_parent_layout);

           //Set bg color
           if(i%2==0)
               pakage_parent_layout.setBackgroundColor(getResources().getColor(R.color.color_choose_plan_green_top));
           else
               pakage_parent_layout.setBackgroundColor(getResources().getColor(R.color.color_choose_plan_green_mid));

           //Set Values in View
           ((TextView)pakage_item.findViewById(R.id.package_name)).setText(packageModelList.get(i).package_name);
           ((TextView)pakage_item.findViewById(R.id.package_price)).setText(packageModelList.get(i).recurring_total);

           //Add button click Listener
           pakage_item.findViewById(R.id.get_number).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(ChoosePlanActivity.this, ChooseNumberActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putParcelable("SelectedPackage",packageModelList.get(position));
                   intent.putExtras(bundle);
                   startActivity(intent);
               }
           });

           //Add View to layout
           linearLayout.addView(pakage_item);
       }
    }

}
