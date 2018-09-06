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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.adapters.NavigationViewItemAdapter;
import com.posfone.promote.posfone.fragment.ContactFragment;
import com.posfone.promote.posfone.fragment.PaymentFragment;
import com.posfone.promote.posfone.fragment.SettingFragment;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

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

        //Open Contact Fragment
        ContactFragment contactFragment = new ContactFragment();
        openFragment(contactFragment,false,"ContactFragment");

        //Get Profile Details
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);
        if(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL)==null)
            getProfileDetails();
        else
            initNavigationViewMenuList();

        //set LoggedIn status
        preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_IS_LOGIN,true);


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
        final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);

        //Init Profile Details
        ((TextView)findViewById(R.id.txt_header_username)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME)+" "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView)findViewById(R.id.txt_header_user_location)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE)+","+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY));
        ((TextView)findViewById(R.id.txt_header_user_contact_number)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER)+"");

        //Init NAvigation Item List
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


        //Sign Out button
        findViewById(R.id.txt_signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Clear All SP Data
                preferenceHandler.clearSP();
                //Redirect user to PreSignInActivity
                Intent intent = new Intent(MainActivity.this,PreSignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
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


    private void getProfileDetails() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        String token = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_TOKEN);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        header.put("token", token);
        //RequestBody

        Call call = RESTClient.call_POST(RESTClient.PROFILE, header, "", new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse",res);
                        final JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {


                            JSONObject user = jsonObject.getJSONObject("user");

                            final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_USERNAME,user.getString("username"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL,user.getString("user_email"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY,user.getString("country"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE,user.getString("state"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME,user.getString("first_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME,user.getString("last_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO,user.getString("profile_photo"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_SESSION_TOKEN,user.getString("session_token"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER,user.getString("phone_number"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER,user.getString("pay729_number"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initNavigationViewMenuList();
                                }
                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //-----
                    }
                } else {
                    //-----------
                }

            }
        });

    }



}
