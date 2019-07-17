package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.remote.models.NubmerCategoryModel;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;


public class ChoosePlanActivity extends AppCompatActivity implements View.OnClickListener {


    private List<NubmerCategoryModel> nubmerCategoryModelList;

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
        txt_title.setText("Choose Number Category");

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
        GeneralUtil.showProgressDialog(this,null);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);


        Call call = RESTClient.call_GET(RESTClient.PLANS, header,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            nubmerCategoryModelList = new ArrayList<>();

                            JSONArray jsonArray = jsonObject.getJSONArray("number_category");
                            for(int i =0;i<jsonArray.length();i++)
                            {
                                NubmerCategoryModel nubmerCategoryModel = new NubmerCategoryModel();

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                nubmerCategoryModel.category_name = jsonObject1.getString("category_name");
                                nubmerCategoryModel.amount = jsonObject1.getString("amount");
                                nubmerCategoryModel.amount_type = jsonObject1.getString("amount_type");
                                nubmerCategoryModel.updated_on = jsonObject1.getString("updated_on");

                                nubmerCategoryModelList.add(nubmerCategoryModel);
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadCategory();
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


    private void loadCategory()
    {
        if(nubmerCategoryModelList==null || nubmerCategoryModelList.size()==0)
            return;

        //get layout Height
       ScrollView scrollView = findViewById(R.id.parentlayout);
       int screenHeight = scrollView.getMeasuredHeight();

        int singleItemHeight =0;
        if(nubmerCategoryModelList.size()>2)
            singleItemHeight = screenHeight/3;
        else
            singleItemHeight = screenHeight/nubmerCategoryModelList.size();

        LinearLayout linearLayout = findViewById(R.id.parentlayout_inner);
       for(int i =0;i<nubmerCategoryModelList.size();i++)
       {
           final int position = i;
           // Changes the height and width to the specified *pixels*
           LinearLayout pakage_item = (LinearLayout) getLayoutInflater().inflate(R.layout.pakage_item, null);
           LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                   singleItemHeight);
           pakage_item.setLayoutParams(lp);
           View pakage_parent_layout  = pakage_item.findViewById(R.id.pakage_parent_layout);

           //Set bg color
           if(i%3==0)
               pakage_parent_layout.setBackgroundColor(getResources().getColor(R.color.color_choose_plan_green_top));
           else if(i%3==1)
               pakage_parent_layout.setBackgroundColor(getResources().getColor(R.color.color_choose_plan_green_mid));
           else if(i%3==2)
               pakage_parent_layout.setBackgroundColor(getResources().getColor(R.color.color_choose_plan_green_bottom));

           //Set Values in View
           ((TextView)pakage_item.findViewById(R.id.package_name)).setText(nubmerCategoryModelList.get(i).category_name );

           //((TextView)pakage_item.findViewById(R.id.package_price)).setText("\u00a3"+" "+nubmerCategoryModelList.get(i).amount);
           ((TextView)pakage_item.findViewById(R.id.package_price)).setVisibility(View.GONE);

           //Add button click Listener
           pakage_item.findViewById(R.id.get_number).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   //Intent intent = new Intent(ChoosePlanActivity.this, ChooseNumberActivity.class);
                   Intent intent = new Intent(ChoosePlanActivity.this, PackageActivity.class);
                   //Bundle bundle = new Bundle();
                   //bundle.putParcelable("SelectedPackage",nubmerCategoryModelList.get(position));
                   //intent.putExtras(bundle);
                   startActivity(intent);
               }
           });

           //Add View to layout
           linearLayout.addView(pakage_item);
       }
    }

}
