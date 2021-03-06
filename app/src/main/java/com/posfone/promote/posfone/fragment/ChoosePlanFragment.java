package com.posfone.promote.posfone.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.posfone.promote.posfone.R;


/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class ChoosePlanFragment extends BaseFragment {

    String fragmentName;
    private View view;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Bundle bundle = this.getArguments();
        //fragmentName = bundle.getString("fragmentName");

        Log.i("PaymentFragment",fragmentName+"Fragment onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("PaymentFragment",fragmentName+"Fragment onCreateView");

        //view = inflater.inflate(R.layout.activity_choose_plan, container, false);

        //initViews();

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

        setTitle("Choose Plan");

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



}
