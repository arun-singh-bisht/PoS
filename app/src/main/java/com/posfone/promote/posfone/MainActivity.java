package com.posfone.promote.posfone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.iid.FirebaseInstanceId;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.Utils.TwilioTokenManager;
import com.posfone.promote.posfone.adapters.NavigationViewItemAdapter;
import com.posfone.promote.posfone.adapters.ConatactPageAdapter;
import com.posfone.promote.posfone.fragment.ContactFragment;
import com.posfone.promote.posfone.fragment.ContactLog;
import com.posfone.promote.posfone.fragment.PaymentFragment;
import com.posfone.promote.posfone.fragment.SettingFragment;
import com.posfone.promote.posfone.model.Contact;
import com.posfone.promote.posfone.rest.ApiClient;
import com.posfone.promote.posfone.rest.RESTClient;
import com.squareup.picasso.Picasso;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    private static final String TAG = "MainActivity";
    CircleImageView profile_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        profile_icon=findViewById(R.id.imageView);
        profile_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Profile Click
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Open Contact Fragment

        //ContactFragment contactFragment = new ContactFragment();
        MainFragments contactFragment=new MainFragments();
        openFragment(contactFragment,false,"MainFragments");

        //Get Profile Details
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);
        /*if(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL)==null)
            getProfileDetails();
        else
            initNavigationViewMenuList();*/

        if (preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USERNAME) == null)
            drawer.openDrawer(Gravity.LEFT);
        getProfileDetails();

        //set LoggedIn status
        preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_IS_LOGIN,true);


        //Get Twilio Access Token and Register this app for receiving Incoming Calls
        retrieveAccessToken();

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
       if(id == android.R.id.home)
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
        navigationViewItemModelList.add(navigationViewItemModel_calls);
        navigationViewItemModelList.add(navigationViewItemModel_contacts);
        navigationViewItemModelList.add(navigationViewItemModel_settings);

        NavigationViewItemAdapter navigationViewItemAdapter = new NavigationViewItemAdapter(this,navigationViewItemModelList);
        listView.setAdapter(navigationViewItemAdapter);


        //Sign Out button
        findViewById(R.id.txt_signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CustomAlertDialog.showDialog(MainActivity.this, "Are you sure?",R.layout.custom_dialo, new CustomAlertDialog.I_CustomAlertDialog() {
                    @Override
                    public void onPositiveClick() {
                        //Clear All SP Data
                        preferenceHandler.clearSP();
                        //Redirect user to PreSignInActivity
                        Intent intent = new Intent(MainActivity.this,PreSignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeClick() {

                    }
                });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProfileDetails();
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
                 MainFragments mainFragments = new MainFragments();
                openFragment(mainFragments,true,"MainFragments");
            }
            break;
            case 3:{
                //Contacts Click
                MainFragments mainFragments = new MainFragments();
                Bundle bundle = new Bundle();
                bundle.putSerializable("contacts", "contacts");
                mainFragments.setArguments(bundle);
                openFragment(mainFragments,true,"MainFragments");
               // getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            break;
            case 4:{
                //Settings Click
                SettingFragment settingFragment = new SettingFragment();
                openFragment(settingFragment,true,"SettingFragment");
            }
            break;

        }
    }

    private void setProfileDetails()
    {
        //Init Profile Details
        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);

        String address = "";
        String city =  preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_CITY);
        String state =  preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE);
        String country =  preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY);
        String postcode =  preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_POSTCODE);



        if(city!=null && city.length()>0 && !city.equalsIgnoreCase("null"))
            address = city+",";
        if(state!=null && state.length()>0 && !state.equalsIgnoreCase("null"))
            address = address+""+state+",";
        if(country!=null && country.length()>0 && !country.equalsIgnoreCase("null"))
            address = address+""+country;
        if(postcode!=null && postcode.length()>0 && !postcode.equalsIgnoreCase("null"))
            address = address+","+postcode;

        ((TextView)findViewById(R.id.txt_header_username)).setText(preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME)+" "+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME));
        ((TextView)findViewById(R.id.txt_header_user_location)).setText(address);
        ((TextView)findViewById(R.id.txt_header_user_contact_number)).setText("+"+preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER));

        ImageView imageView =  findViewById(R.id.imageView);
        //Load New Image in Profile Pic
        String profile_pic_url = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO);

            Picasso.with(MainActivity.this)
                    .load(profile_pic_url)
                    .placeholder(R.drawable.blank_profile_image)
                    .into(imageView);
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
                            System.out.println("all details  "+user);

                            final SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(MainActivity.this);
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_USERNAME,user.getString("username"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL,user.getString("user_email"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_COUNTRY,user.getString("country"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_STATE,user.getString("state"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_POSTCODE,user.getString("zipcode"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_CITY,user.getString("city"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_FIRST_NAME,user.getString("first_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_LAST_NAME,user.getString("last_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHOTO,user.getString("profile_photo"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_SESSION_TOKEN,user.getString("session_token"));

                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER,user.getString("pay729_number"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PHONE_NUMBER,user.getString("phone_number"));
                            JSONObject package_detail =  jsonObject.getJSONObject("package_detail");
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PACKAGE_NAME,package_detail.getString("package_name"));
                            preferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_PROFILE_PACKAGE_EXPIRE_DATE,package_detail.getString("expiry_date"));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setProfileDetails();
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


    //Register For Twilio Incoming Calls
    /*
     * Get an access token from your Twilio access token server
     */
    private void retrieveAccessToken() {

        if(new TwilioTokenManager(MainActivity.this).isTokenValid())
            return;

        Ion.with(this).load(RESTClient.TWILIO_ACCESS_TOKEN_SERVER_URL + "?identity=" + "Alice").asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String accessToken) {
                if (e == null) {
                    Log.d(TAG, "Access token: " + accessToken);
                    //Store Access Token in preference
                    new TwilioTokenManager(MainActivity.this).saveToken(accessToken);
                    registerForCallInvites();
                } else {
                    GeneralUtil.showToast(MainActivity.this,"Error retrieving access token. Unable to make calls");
                }
            }
        });
    }
    /*
     * Register your FCM token with Twilio to receive incoming call invites
     *
     * If a valid google-services.json has not been provided or the FirebaseInstanceId has not been
     * initialized the fcmToken will be null.
     *
     * In the case where the FirebaseInstanceId has not yet been initialized the
     * VoiceFirebaseInstanceIDService.onTokenRefresh should result in a LocalBroadcast to this
     * activity which will attempt registerForCallInvites again.
     *
     */
    private void registerForCallInvites() {
        //Get FCM token for this app's user
        final String fcmToken = FirebaseInstanceId.getInstance().getToken();
        if (fcmToken != null) {
            Log.i(TAG, "Registering with FCM");
            //Get saved Twilio access token from preference
            final TwilioTokenManager twilioTokenManager = new TwilioTokenManager(MainActivity.this);
            String accessToken = twilioTokenManager.getToken();
            //Register FCM to current Access Token
            Voice.register(this, accessToken, Voice.RegistrationChannel.FCM, fcmToken, new RegistrationListener() {
                @Override
                public void onRegistered(String accessToken, String fcmToken) {
                    Log.d(TAG, "Successfully registered FCM " + fcmToken);
                }

                @Override
                public void onError(RegistrationException error, String accessToken, String fcmToken) {
                    String message = String.format("Registration Error: %d, %s", error.getErrorCode(), error.getMessage());
                    Log.e(TAG, message);
                    GeneralUtil.showToast(MainActivity.this,message);
                }
            });
        }
    }

}
