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

import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.ui.adapters.PaymentListAdapter;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class PaymentFragment extends BaseFragment implements AdapterView.OnItemClickListener{

    String fragmentName;
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

        PaymentListAdapter paymentListAdapter = new PaymentListAdapter(getActivity(),null);
        ListView listView =  (ListView) view.findViewById(R.id.list_payment);
        listView.setOnItemClickListener(this);
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
}
