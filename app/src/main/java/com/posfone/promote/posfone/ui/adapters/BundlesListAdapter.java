package com.posfone.promote.posfone.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class BundlesListAdapter extends FragmentPagerAdapter {
         Context context;
        ArrayList<Fragment> fragments=new ArrayList<>();
        ArrayList<String> tabtitles= new ArrayList<>();

        public void addFragments(Fragment fragments, String tabtitles){
            this.fragments.add(fragments);
            this.tabtitles.add(tabtitles);
        }

        public BundlesListAdapter(Context context, FragmentManager fm){
            super(fm);
            this.context=context;
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabtitles.get(position);
        }
    }
