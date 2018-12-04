package com.posfone.promote.posfone.stripe;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.posfone.promote.posfone.ManageNumberActivity;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.SignInActivity;
import com.posfone.promote.posfone.SummeryActivity;
import com.posfone.promote.posfone.Utils.CustomAlertDialog;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.Utils.SharedPreferenceHandler;
import com.posfone.promote.posfone.rest.ApiClient;

import com.posfone.promote.posfone.rest.RESTClient;
import com.posfone.promote.posfone.stripe.utils.ui.CircleImageView;
import com.posfone.promote.posfone.stripe.utils.ui.StripeImageView;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import com.daimajia.numberprogressbar.NumberProgressBar;

import com.daimajia.numberprogressbar.OnProgressBarListener;



import java.util.Timer;

import java.util.TimerTask;
/**
 *
 * Created by alan Lam on 31/7/16.
 */
public  class StripeBaseActivity extends AppCompatActivity  implements OnProgressBarListener{


    // ----------Progress Bar
    private Timer timer;
    public View view;
    private NumberProgressBar bnp;

    private String redirect_from;
    String name="";
    private String package_name="";
    private String total="";
    private String package_id="";
    private String txn_id="";

    // VARIABLE
    private String mLastInput;
    private String mShopName = "PosFone";
    private String image_url="https://picsum.photos/200/300/?random";
    private String mDescription = "Package Subscription";
    private String mCurrency = "GBP";
    private String mEmail = "";

    private static boolean shouldAllowBack=true;


    //Ui----------------------------
    private CoordinatorLayout relativeLayout;
    private LinearLayout mStripe_dialog_card_container;
    private LinearLayout mStripe_dialog_date_container;
    private LinearLayout mStripe_dialog_cvc_container;
    private LinearLayout mStripe_dialog_email_container;
    private EditText mCreditCard;
    private EditText mExpiryDate;
    private EditText mCVC;
    private ImageView mStripeDialogCardIcon;
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private TextView mErrorMessage;
    private TextView packageName;
    private TextView selected;
    private TextView pay_number;
    private TextView mEmailTextView;
    public CircleImageView mShopImageView;
    private Button mStripe_dialog_paybutton;
    private StripeImageView mExitButton;


//-------------------
private Stripe mStripe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe__dialog_);
        // Progress Bar
        bnp = (NumberProgressBar)findViewById(R.id.number_progress_bar);
        timer = new Timer();
        bnp.setOnProgressBarListener(this);
        // Getting package Details
        redirect_from=getIntent().getStringExtra("redirect_from");
        package_id=getIntent().getStringExtra("package_id");
        package_name=getIntent().getStringExtra("package_name");
        txn_id=getIntent().getStringExtra("txn_id");
        total=getIntent().getStringExtra("total");
        //widgets-----------------
        relativeLayout=findViewById(R.id.pay_layout);
        mStripe_dialog_card_container = (LinearLayout) findViewById(R.id.stripe_dialog_card_container);
        mStripe_dialog_date_container = (LinearLayout) findViewById(R.id.stripe_dialog_date_container);
        mStripe_dialog_cvc_container = (LinearLayout) findViewById(R.id.stripe_dialog_cvc_container);
        mStripe_dialog_email_container = (LinearLayout) findViewById(R.id.stripe_dialog_email_container);
       // mExitButton = (StripeImageView) findViewById(R.id.stripe_dialog_exit);
        mTitleTextView = (TextView) findViewById(R.id.stripe_dialog_txt1);
        mDescriptionTextView = (TextView) findViewById(R.id.stripe_dialog_txt2);
        packageName=(TextView)findViewById(R.id.txt_plan_type);
        pay_number=findViewById(R.id.txt_plan_exire_date);

        mEmailTextView = (TextView) findViewById(R.id.stripe_dialog_email);
        mErrorMessage = (TextView) findViewById(R.id.stripe_dialog_error);
        //mShopImageView = findViewById(R.id.stripe__logo);
        mExpiryDate = (EditText) findViewById(R.id.stripe_dialog_date);
        mCreditCard = (EditText) findViewById(R.id.stripe_dialog_card);
        mCVC = (EditText) findViewById(R.id.stripe_dialog_cvc);
        mStripe_dialog_paybutton = (Button) findViewById(R.id.stripe_dialog_paybutton);
        selected=findViewById(R.id.btn_upgrade_plan);
        mStripeDialogCardIcon = (ImageView) findViewById(R.id.stripe_dialog_card_icon);
        //-----------------------------
       // initView(savedInstanceState);
       // init(savedInstanceState);
        text_card();
    }

    //on destroy
    @Override

    protected void onDestroy() {

        super.onDestroy();

        timer.cancel();

    }

// Test Card---------------------------------------
    private void text_card() {
        try {
            mStripe = new Stripe(this,ApiClient.mDefaultPublishKey);
        }catch (Exception e){

        }
        SharedPreferenceHandler sharedPreferenceHandler=new SharedPreferenceHandler(this);
        name=sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USERNAME);
        String number=sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_NEW_PAY_729_NUMBER);
        mEmail=sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL);
        String number_retain=sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER);
        //mShopImageView.setUrl(image_url);
        mTitleTextView.setText(mShopName);
        pay_number.setText(package_name);
        if(number!=null){
        selected.setText(number);
        //sharedPreferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_NEW_PAY_729_NUMBER,null);
        }
        else
            selected.setText(number_retain);
        mDescriptionTextView.setText(mDescription);
        mStripe_dialog_paybutton.setText(getString(R.string.__stripe_pay) + " " + "\u00a3 " + (total));
        mStripe_dialog_paybutton.setOnClickListener(mPayClickListener);
        if(mEmail != null && mEmail.length() > 0){
            mEmailTextView.setText(mEmail);
            mStripe_dialog_email_container.setVisibility(View.VISIBLE);
        }

        mExpiryDate.addTextChangedListener(mCreditCardExpireDateTextWatcher);
        mCreditCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mStripe_dialog_card_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_top);
                }else{
                    mStripe_dialog_card_container.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
        mExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mStripe_dialog_date_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_left_bottom);
                }else{
                    mStripe_dialog_date_container.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
        mCVC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mStripe_dialog_cvc_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_right_bottom);
                }else{
                    mStripe_dialog_cvc_container.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
        mCreditCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mCreditCard.getText().length() > 0) {
                    Card mmCard = new Card(mCreditCard.getText().toString(), 0, 0, "");
                    switch (mmCard.getType()) {
                        case Card.VISA:
                            mStripeDialogCardIcon.setImageResource(R.drawable.ic__visa);
                            mStripeDialogCardIcon.setVisibility(View.VISIBLE);
                            break;
                        case Card.MASTERCARD:
                            mStripeDialogCardIcon.setImageResource(R.drawable.ic__mastercard);
                            mStripeDialogCardIcon.setVisibility(View.VISIBLE);
                            break;
                        case Card.AMERICAN_EXPRESS:
                            mStripeDialogCardIcon.setImageResource(R.drawable.ic__ae);
                            mStripeDialogCardIcon.setVisibility(View.VISIBLE);
                            break;
                        default:
                            mStripeDialogCardIcon.setVisibility(View.GONE);
                    }
                }else{
                    mStripeDialogCardIcon.setVisibility(View.GONE);
                }
            }
        });


    }

    //----------------------------------------------------
    // Checking card  ------------

    /**
     * Credit Card Edittext Change Listener
     */

    private TextWatcher mCreditCardExpireDateTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String input = s.toString();
            SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.GERMANY);
            Calendar expiryDateDate = Calendar.getInstance();
            try {
                expiryDateDate.setTime(formatter.parse(input));
            } catch (ParseException e) {
                if (s.length() == 2 && !mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        mExpiryDate.setText(mExpiryDate.getText().toString() + "/");
                        mExpiryDate.setSelection(mExpiryDate.getText().toString().length());
                    }else{
                        mExpiryDate.setText(mExpiryDate.getText().toString().substring(0,1));
                        mExpiryDate.setSelection(mExpiryDate.getText().toString().length());
                    }
                } else if (s.length() == 2 && mLastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        mExpiryDate.setText(mExpiryDate.getText().toString().substring(0, 1));
                        mExpiryDate.setSelection(mExpiryDate.getText().toString().length());
                    } else {
                        mExpiryDate.setText("");
                        mExpiryDate.setSelection(mExpiryDate.getText().toString().length());
                    }
                } else if (s.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        mExpiryDate.setText("0" + mExpiryDate.getText().toString() + "/");
                        mExpiryDate.setSelection(mExpiryDate.getText().toString().length());
                    }
                } else {

                }
                mLastInput = mExpiryDate.getText().toString();
                return;
            }
        }
    };


    /**
     * On Submit Payment Listener
     */
    private View.OnClickListener mPayClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mErrorMessage.setVisibility(View.GONE);
            if(mCreditCard.getText().toString().length() <= 0){
                anim();
                mCreditCard.setError(getString(R.string.__stripe_invalidate_card_number));
                return;
            }
            if(mCVC.getText().toString().length() <= 0){
               anim();
                mCVC.setError(getString(R.string.__stripe_invalidate_cvc));
                return;
            }
            if(mExpiryDate.getText().toString().length() <= 0){
                anim();
                mExpiryDate.setError(getString(R.string.__stripe_invalidate_expirydate));
                return;
            }
            String mmExpireDate = mExpiryDate.getText().toString();
            String[] mmMMYY = mmExpireDate.split("/");

            Card mmCard = new Card(
                    mCreditCard.getText().toString(),
                    Integer.parseInt(mmMMYY[0]),
                    Integer.parseInt(mmMMYY[1]),
                    mCVC.getText().toString());
            if(mmCard.validateCard()) {
                mStripe_dialog_paybutton.setEnabled(false);
                mStripe_dialog_paybutton.setText("Please Wait ...");
                mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.color_phone_number));
                shouldAllowBack=false;
                bnp.setVisibility(View.VISIBLE);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bnp.incrementProgressBy(1);
                            }
                        });
                    }
                }, 2000, 100);

                mStripe.createToken(mmCard, ApiClient.mDefaultPublishKey, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        if (error != null && error.getMessage().length() > 0) {
                            mErrorMessage.setText(error.getLocalizedMessage());
                            mErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onSuccess(Token token) {
                        String cvc = mCVC.getText().toString();


                        System.out.println("Id  "+token.getId()+"  account "+token.getBankAccount()+"  created  "+token.getCard()+token.getCreated()+"  used "+token.getUsed());

                        boolean isDebitSuccess = StripePaymentService.debit(token,total,txn_id,package_name,package_id,name,selected.getText().toString());
                        if(isDebitSuccess)
                        {
                            mStripe_dialog_paybutton.setText("Succesful Transaction");
                            mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.color_light_green));
                            System.out.println("Transaction Successful!");
                            purchaseTrialPakage();
                        }
                        else
                        {
                            mStripe_dialog_paybutton.setText("Transaction Failed");
                            mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.error_red));
                            bnp.setVisibility(View.INVISIBLE);

                            System.out.println("Unsuccesful TRansaction ");
                        }

                    }
                });
            }else if (!mmCard.validateNumber()) {
                anim();
                mCreditCard.setError(getString(R.string.__stripe_invalidate_card_number));
            } else if (!mmCard.validateExpiryDate()) {
                anim();
                mExpiryDate.setError(getString(R.string.__stripe_invalidate_expirydate));
            } else if (!mmCard.validateCVC()) {
                anim();
                mCVC.setError(getString(R.string.__stripe_invalidate_cvc));
            } else {
                anim();
                mErrorMessage.setText(R.string.__stripe_invalidate_card_detail);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    };

//----------Animation method
    public void anim(){
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        findViewById(R.id.shake_effect).startAnimation(shake);
    }

    //--------------------------Purchase package

    private void purchaseTrialPakage() {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this,null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String,String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        Log.i("userid",userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("package_id",package_id);
        jsonObject.addProperty("txn_id",txn_id);
        String body = "json="+jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER_PURCHASE, header, body, new okhttp3.Callback() {
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
                        JSONObject jsonObject = new JSONObject(res);
                        final String message="Transaction Succesfull!"+"\n Do you Wish to save Your Card";

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomAlertDialog.showDialogSingleButton(StripeBaseActivity.this, message, new CustomAlertDialog.I_CustomAlertDialog() {
                                        @Override
                                        public void onPositiveClick() {

                                            Intent intent = new Intent(StripeBaseActivity.this,ManageNumberActivity.class);
                                            // System.out.println(redirect_from);
                                            if("profile_screen_positive_click".equals(redirect_from)){
                                                Intent myintent = new Intent(StripeBaseActivity.this,SignInActivity.class);
                                                startActivity(myintent);
                                                finish();
                                            }else if("profile_screen".equals(redirect_from)){
                                                intent.putExtra("redirect_from","profile_screen");
                                                startActivity(intent);}
                                            else
                                                startActivity(intent);

                                        }

                                        @Override
                                        public void onNegativeClick() {

                                            Intent intent = new Intent(StripeBaseActivity.this,ManageNumberActivity.class);
                                            // System.out.println(redirect_from);
                                            if("profile_screen_positive_click".equals(redirect_from)){
                                                Intent myintent = new Intent(StripeBaseActivity.this,SignInActivity.class);
                                                startActivity(myintent);
                                                finish();
                                            }else if("profile_screen".equals(redirect_from)){
                                                intent.putExtra("redirect_from","profile_screen");
                                                startActivity(intent);}
                                            else
                                                startActivity(intent);


                                        }
                                    });

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

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack) {
          // create instance
            Toast toast = Toast.makeText(this, "Transaction in Process", Toast.LENGTH_SHORT);
          // set message color
            TextView textView= (TextView) toast.getView().findViewById(android.R.id.message);
            textView.setTextColor(getResources().getColor(R.color.cardview_light_background));
          // set background color
            toast.getView().setBackgroundColor(getResources().getColor(R.color.color_light_green));
        } else {
             super.onBackPressed();
        }
    }

    @Override
    public void onProgressChange(int current, int max) {
        if(current == max) {
            //Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
            bnp.setProgress(0);
        }
    }
}
