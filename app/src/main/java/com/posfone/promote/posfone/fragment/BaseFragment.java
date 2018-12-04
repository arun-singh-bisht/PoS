package com.posfone.promote.posfone.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.posfone.promote.posfone.MainActivity;

public class BaseFragment extends Fragment {


    public void setTitle(String title)
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setScreenTitle(title);
    }

    public void setDisplayHomeAsUpEnabled(boolean b)
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setDisplayHomeAsUpEnabled(b);

    }
}
