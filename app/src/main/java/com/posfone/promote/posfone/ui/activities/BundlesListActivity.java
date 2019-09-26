package com.posfone.promote.posfone.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.models.BundlesModel;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.adapters.BundlesListAdapter;
import com.posfone.promote.posfone.ui.fragments.BundleHistoryFragment;
import com.posfone.promote.posfone.ui.fragments.BundleNewFragment;

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

public class BundlesListActivity extends AppCompatActivity {

    @BindView(R.id.img_right)
    View img_right;
    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    BundlesListAdapter bundlesListAdapter;
    BundleHistoryFragment bundleHistoryFragment;
    BundleNewFragment bundleNewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundles_list);

        ButterKnife.bind(this);
        img_right.setVisibility(View.GONE);
        txt_title.setText("TopUp Call Bundles");

        bundlesListAdapter = new BundlesListAdapter(this, getSupportFragmentManager());
        bundleHistoryFragment = new BundleHistoryFragment();
        bundleNewFragment = new BundleNewFragment();

        bundlesListAdapter.addFragments(bundleHistoryFragment, "Purchase History");
        bundlesListAdapter.addFragments(bundleNewFragment, "Buy New");
        viewPager.setAdapter(bundlesListAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        getBundlesList();
    }

    @OnClick(R.id.img_left)
    public void onBackArrowClick() {
        finish();
    }

    private void showProgress(final boolean doShow) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bundleHistoryFragment.showProgress(doShow);
                bundleNewFragment.showProgress(doShow);
            }
        });
    }

    private void setBundleListData(final List<BundlesModel> historyBundlesModelList,final List<BundlesModel> newBundlesModelList)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bundleHistoryFragment.loadData(historyBundlesModelList);
                bundleNewFragment.loadData(newBundlesModelList);
            }
        });
    }

    private void getBundlesList() {
        //showProgress(true);
        final List<BundlesModel> historyBundlesModelList = new ArrayList<>();
        final List<BundlesModel> newBundlesModelList = new ArrayList<>();

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        //RequestBody
        Log.e("param", ApiClient.X_API_KEY + " -> " + userID);
        Call call = RESTClient.call_POST(RESTClient.USER_BUNDLES, header, "", new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showProgress(false);
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                Log.e("code", String.valueOf(response.code()));
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        Log.i("onResponse", res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("success") && jsonObject.getString("success").equalsIgnoreCase("1")) {


                            JSONArray topupbundleArray = jsonObject.getJSONArray("topupbundle");
                            if(topupbundleArray!=null && topupbundleArray.length()>0)
                            {
                                for(int i=0;i<topupbundleArray.length();i++)
                                {
                                    BundlesModel bundlesModel = new BundlesModel();
                                    JSONObject topupbundle  = topupbundleArray.getJSONObject(i);
                                    bundlesModel.setBaseprice(topupbundle.getString("baseprice"));
                                    bundlesModel.setBundlename(topupbundle.getString("bundlename"));
                                    bundlesModel.setBundletime(topupbundle.getString("bundletime"));
                                    bundlesModel.setBundletimemin(topupbundle.getString("bundletimemin"));
                                    bundlesModel.setCreated_on(topupbundle.getString("created_on"));
                                    newBundlesModelList.add(bundlesModel);
                                }
                            }

                            JSONArray userbundleArray = jsonObject.getJSONArray("userbundle");
                            if(userbundleArray!=null && userbundleArray.length()>0)
                            {
                                for(int i=0;i<userbundleArray.length();i++)
                                {
                                    BundlesModel bundlesModel = new BundlesModel();
                                    JSONObject topupbundle  = userbundleArray.getJSONObject(i);
                                    bundlesModel.setBaseprice(topupbundle.getString("baseprice"));
                                    bundlesModel.setBundlename(topupbundle.getString("bundlename"));
                                    bundlesModel.setBundletime(topupbundle.getString("bundletime"));
                                    bundlesModel.setBundletimemin(topupbundle.getString("bundletimemin"));
                                    bundlesModel.setCreated_on(topupbundle.getString("created_on"));
                                    historyBundlesModelList.add(bundlesModel);
                                }
                            }

                            setBundleListData(historyBundlesModelList,newBundlesModelList);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    Toast.makeText(BundlesListActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                }
                showProgress(false);
            }
        });
    }
}
