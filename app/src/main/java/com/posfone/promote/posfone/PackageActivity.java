package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.model.PackageModel;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


public class PackageActivity extends AppCompatActivity implements View.OnClickListener {

    private String isTrial;
    private String redirect_from;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        initViews();
    }

    private void initViews()
    {
        redirect_from=getIntent().getStringExtra("redirect_from");
        isTrial = getIntent().getStringExtra("isTrial");
        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Packages");
        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        GetPackageDeails();

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

    private void showPackageDetails(final List<HashMap<String,String>> packageModelList)
    {
        if(packageModelList==null || packageModelList.size()==0)
            return;

        LinearLayout linearLayout = findViewById(R.id.package_layout);

        for(int i=0;i<packageModelList.size();i++)
        {
            final HashMap<String,String> packageModel = packageModelList.get(i);

            // Redirecting to next Page if package is Trial
            if(packageModelList.size()==1){
                String name=packageModelList.get(0).get("Package Name");
                if ("Stripe Trail".equals(name)){
                    Intent intent = new Intent(PackageActivity.this,PackageDetailActivity.class);
                    intent.putExtra("redirect_from",redirect_from);
                    Bundle extras = new Bundle();
                    extras.putSerializable("SelectedPackage",packageModel);
                    intent.putExtras(extras);
                    PackageActivity.this.finish();
                    startActivity(intent);
                }
            }
            //--------------------------------------------

            View view = getLayoutInflater().inflate(R.layout.package_details_row,null);
            TextView package_name = view.findViewById(R.id.package_name);
            TextView gateway_name = view.findViewById(R.id.gateway_name);
            TextView price = view.findViewById(R.id.price);
            TextView duration = view.findViewById(R.id.duration);

            package_name.setText(packageModel.get("Package Name"));
            gateway_name.setText(packageModel.get("gatewayName"));
            price.setText("\u00a3"+packageModel.get("Subscription Charge")+" / ");
            duration.setText("Per Month");
            linearLayout.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PackageActivity.this,PackageDetailActivity.class);
                    intent.putExtra("redirect_from",redirect_from);
                    Bundle extras = new Bundle();
                    extras.putSerializable("SelectedPackage",packageModel);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });


        }


    }


    private void GetPackageDeails() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("isTrial",isTrial);
        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.PACKAGES, header, body, new okhttp3.Callback() {
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


                            final List<HashMap<String,String>> packageModelList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("packages");

                            for(int i =0;i<jsonArray.length();i++)
                            {
                                HashMap<String,String> hashMap = new HashMap<>();

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String packageId = jsonObject1.getString("package_id");
                                hashMap.put("packageId",packageId);

                                JSONObject gateway = jsonObject1.getJSONObject("gateway");
                                String gatewayName = gateway.getString("name");
                                hashMap.put("gatewayName",gatewayName);

                                JSONArray package_pay = jsonObject1.getJSONArray("package_pay");
                                for(int j=0;j<package_pay.length();j++)
                                {
                                    JSONObject jsonObject2 = package_pay.getJSONObject(j);
                                    String key = jsonObject2.getString("key");
                                    String value = jsonObject2.getString("value");
                                    hashMap.put(key,value);
                                    String key_order =hashMap.get("key_order");
                                    if(key_order==null)
                                        hashMap.put("key_order",key);
                                    else
                                        hashMap.put("key_order",key_order+"*"+key);
                                }

                                packageModelList.add(hashMap);
                            }
                            if(jsonArray.length()==1){

                            }
                             runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showPackageDetails(packageModelList);
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
