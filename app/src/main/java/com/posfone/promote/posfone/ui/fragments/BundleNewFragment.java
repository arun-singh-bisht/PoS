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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.data.local.db.DatabaseHelper;
import com.posfone.promote.posfone.data.remote.models.BundlesModel;
import com.posfone.promote.posfone.ui.activities.Dialer;
import com.posfone.promote.posfone.ui.adapters.GenericListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BundleNewFragment extends Fragment {

    ProgressBar progress_bar;
    GenericListAdapter genericListAdapter;
    ListView listView;
    TextView text_no_record_found;
    public BundleNewFragment() {
        // Required empty public constructor
    }

    public static BundleNewFragment newInstance(String param1, String param2) {
        BundleNewFragment fragment = new BundleNewFragment();

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
        View v=inflater.inflate(R.layout.fragment_contact_log, container, false);
        v.findViewById(R.id.dialer).setVisibility(View.GONE);
        listView = v.findViewById(R.id.mlistView);
        text_no_record_found = v.findViewById(R.id.text_no_record_found);
        progress_bar = v.findViewById(R.id.progress_bar);
        showProgress(true);
        return v;
    }

    @Override
    public void onResume(){
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

                view.findViewById(R.id.view_buy_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.view_purchase_date).setVisibility(View.GONE);


                text_bundle_name_value.setText(listData.get(position).getBundlename());
                text_price_value.setText("£ "+listData.get(position).getBaseprice());
                text_time_value.setText(listData.get(position).getBundletime()+" | "+listData.get(position).getBundletimemin());
                return view;
            }
        };
        listView.setAdapter(genericListAdapter);
    }
}
