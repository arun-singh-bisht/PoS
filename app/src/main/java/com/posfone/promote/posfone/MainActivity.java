package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.posfone.promote.posfone.adapters.NavigationViewItemAdapter;
import com.posfone.promote.posfone.fragment.ChoosePlanFragment;
import com.posfone.promote.posfone.fragment.ContactFragment;
import com.posfone.promote.posfone.fragment.PaymentFragment;
import com.posfone.promote.posfone.fragment.SettingFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Load Side Menu Items
        initNavigationViewMenuList();


        //Open Contact Fragment
        ContactFragment contactFragment = new ContactFragment();
        openFragment(contactFragment,false,"ContactFragment");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home)
        {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    * Prepare Navigation View's menu list
    * */
    public void initNavigationViewMenuList()
    {
        ListView listView =  findViewById(R.id.list_menu_items);
        listView.setOnItemClickListener(this);

        List<NavigationViewItemAdapter.NavigationViewItemModel> navigationViewItemModelList = new ArrayList<NavigationViewItemAdapter.NavigationViewItemModel>() {};

        //Profile
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_profile = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_profile.item_name = "Profile";
        navigationViewItemModel_profile.res_icon = R.drawable.side_menu_profile_icon;
        //Payment
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_payment = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_payment.item_name = "Payments";
        navigationViewItemModel_payment.res_icon = R.drawable.side_menu_payment;
        //Messages
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_messages = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_messages.item_name = "Messages";
        navigationViewItemModel_messages.res_icon = R.drawable.side_menu_message;
        //Calls
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_calls = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_calls.item_name = "Calls";
        navigationViewItemModel_calls.res_icon = R.drawable.side_menu_call;
        //Contacts
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_contacts = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_contacts.item_name = "Contacts";
        navigationViewItemModel_contacts.res_icon = R.drawable.side_menu_contact;
        //Settings
        NavigationViewItemAdapter.NavigationViewItemModel navigationViewItemModel_settings = new NavigationViewItemAdapter.NavigationViewItemModel();
        navigationViewItemModel_settings.item_name = "Settings";
        navigationViewItemModel_settings.res_icon = R.drawable.side_menu_setting;

        navigationViewItemModelList.add(navigationViewItemModel_profile);
        navigationViewItemModelList.add(navigationViewItemModel_payment);
        navigationViewItemModelList.add(navigationViewItemModel_messages);
        navigationViewItemModelList.add(navigationViewItemModel_calls);
        navigationViewItemModelList.add(navigationViewItemModel_contacts);
        navigationViewItemModelList.add(navigationViewItemModel_settings);

        NavigationViewItemAdapter navigationViewItemAdapter = new NavigationViewItemAdapter(this,navigationViewItemModelList);
        listView.setAdapter(navigationViewItemAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (i)
        {
            case 0:{
                //Profile Click
                Intent intent = new Intent(this,ProfileActivity.class);
                startActivity(intent);
            }
            break;
            case 1:{
                //Payment Click
                PaymentFragment paymentFragment = new PaymentFragment();
                openFragment(paymentFragment,true,"PaymentFragment");
            }
            break;
            case 2:{
                //Messages Click
            }
            break;
            case 3:{
                //Calls Click
            }
            break;
            case 4:{
                //Contacts Click
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            break;
            case 5:{
                //Settings Click
                SettingFragment settingFragment = new SettingFragment();
                openFragment(settingFragment,true,"SettingFragment");
            }
            break;

        }
    }

    private void openFragment(Fragment fragment,boolean isAddToBackStack,String TAG)
    {

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        if(isAddToBackStack)
            fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit();
    }

    public void setScreenTitle(String title)
    {
        TextView txt_screen_title = findViewById(R.id.txt_screen_title);
        txt_screen_title.setText(title);
    }

    public void setDisplayHomeAsUpEnabled(boolean b)
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(b);
    }


}
