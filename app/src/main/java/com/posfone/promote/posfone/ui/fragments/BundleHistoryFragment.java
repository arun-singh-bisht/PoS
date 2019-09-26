package com.posfone.promote.posfone.ui.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.db.DatabaseHelper;
import com.posfone.promote.posfone.data.local.models.PaymentModel;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.models.BundlesModel;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.ui.activities.Dialer;
import com.posfone.promote.posfone.ui.adapters.GenericListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

public class BundleHistoryFragment extends Fragment {

    ProgressBar progress_bar;
    GenericListAdapter genericListAdapter;
    ListView listView;
    TextView text_no_record_found;
    public BundleHistoryFragment() {
        // Required empty public constructor
    }

    public static BundleHistoryFragment newInstance(String param1, String param2) {
        BundleHistoryFragment fragment = new BundleHistoryFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_log, container, false);
        listView = v.findViewById(R.id.mlistView);
        v.findViewById(R.id.dialer).setVisibility(View.GONE);
        text_no_record_found = v.findViewById(R.id.text_no_record_found);
        progress_bar = v.findViewById(R.id.progress_bar);
        showProgress(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showProgress(final boolean doShow) {
        progress_bar.setVisibility(doShow ? View.VISIBLE : View.GONE);
    }

    public void loadData(final List<BundlesModel> listData )
    {
        if(listData==null || listData.size()==0) {
            text_no_record_found.setVisibility(View.VISIBLE);
            return;
        }
        genericListAdapter = new GenericListAdapter(getActivity(),listData.size(),R.layout.bundle_list_item){

            @Override
            public View initGenericView(View view, int position) {
                TextView text_bundle_name_value = (TextView) view.findViewById(R.id.text_bundle_name_value);
                TextView text_price_value = (TextView) view.findViewById(R.id.text_price_value);
                TextView text_time_value = (TextView) view.findViewById(R.id.text_time_value);

                view.findViewById(R.id.view_buy_btn).setVisibility(View.GONE);
                view.findViewById(R.id.view_purchase_date).setVisibility(View.VISIBLE);

                TextView text_purchase_date_value = (TextView) view.findViewById(R.id.text_purchase_date_value);

                text_bundle_name_value.setText(listData.get(position).getBundlename());
                text_price_value.setText("£ "+listData.get(position).getBaseprice());
                text_time_value.setText(listData.get(position).getBundletime()+" | "+listData.get(position).getBundletimemin());
                text_purchase_date_value.setText(listData.get(position).getCreated_on());

                return view;
            }
        };
        listView.setAdapter(genericListAdapter);
    }

}
