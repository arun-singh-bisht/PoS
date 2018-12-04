package com.posfone.promote.posfone;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.posfone.promote.posfone.adapters.ConatactPageAdapter;
import com.posfone.promote.posfone.fragment.ContactFragment;
import com.posfone.promote.posfone.fragment.ContactLog;

import butterknife.ButterKnife;

public class MainFragments extends Fragment {

    TabLayout tabLayout;
    ViewPager viewPager;
    ConatactPageAdapter viewPageAdapter;
    private View view;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // Log.i("PaymentFragment",fragmentName+"Fragment onCreateView");

        Bundle bundle = getArguments();
        String data=null;
        view = inflater.inflate(R.layout.activity_main_fragments, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        viewPageAdapter = new ConatactPageAdapter(getActivity(),getChildFragmentManager());
        viewPageAdapter.addFragments(new ContactLog(), "RECENT");
        viewPageAdapter.addFragments(new ContactFragment(), "CONTACT");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        if(bundle!=null) {
             viewPager.setCurrentItem(2);
        }

        ButterKnife.bind(this,view);


        //initViews();
        return view;
    }

}
