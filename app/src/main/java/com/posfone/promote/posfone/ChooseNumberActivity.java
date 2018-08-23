package com.posfone.promote.posfone;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.posfone.promote.posfone.fragment.NumberFragment;
import com.posfone.promote.posfone.fragment.PaymentFragment;
import com.posfone.promote.posfone.model.CountryModel;
import com.posfone.promote.posfone.model.PackageModel;

import java.util.ArrayList;
import java.util.List;


public class ChooseNumberActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private NumberFragment numberFragment_type;
    private NumberFragment paymentFragment_country;
    private PackageModel packageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number);

        initViews();
    }

    private void initViews()
    {
        packageModel = (PackageModel)getIntent().getParcelableExtra("SelectedPackage");

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Choose Number");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);


        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        paymentFragment_country = new NumberFragment();
        paymentFragment_country.setTabName(NumberFragment.TAB_COUNTRY);
        adapter.addFrag(paymentFragment_country, "Country");

        numberFragment_type = new NumberFragment();
        numberFragment_type.setTabName(NumberFragment.TAB_TYPE);
        adapter.addFrag(numberFragment_type, "Type");

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            System.out.println("Fragment getItem "+position);
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            System.out.println("Fragment addFrag "+title);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            System.out.println("Fragment getPageTitle "+position);
            return mFragmentTitleList.get(position);
        }
    }

    public void scrollToNextTab(CountryModel countryModel)
    {
        viewPager.setCurrentItem(1,true);
        numberFragment_type.loadData(countryModel);
    }

    public PackageModel getSelectedPackage()
    {
        return packageModel;
    }
}
