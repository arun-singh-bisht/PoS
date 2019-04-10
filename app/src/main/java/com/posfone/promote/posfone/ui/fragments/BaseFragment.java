package com.posfone.promote.posfone.ui.fragments;

import android.support.v4.app.Fragment;

import com.posfone.promote.posfone.ui.activities.MainActivity;

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
