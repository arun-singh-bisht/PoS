package com.example.poscall.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.poscall.R;

/**
 * Created by Arun.Singh on 7/20/2018.
 */

public class PaymentFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("PaymentFragment","onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("PaymentFragment","onCreateView");
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onPause() {
        Log.i("PaymentFragment","onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("PaymentFragment","onStop");
        super.onStop();
    }


    @Override
    public void onDestroy() {
        Log.i("PaymentFragment","onDestroy");
        super.onDestroy();
    }
}
