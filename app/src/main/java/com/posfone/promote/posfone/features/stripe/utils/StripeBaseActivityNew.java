package com.posfone.promote.posfone.features.stripe.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.features.stripe.utils.ui.CircleImageView;
import com.posfone.promote.posfone.features.stripe.utils.ui.StripeImageView;
import com.posfone.promote.posfone.ui.activities.ManageNumberActivity;
import com.posfone.promote.posfone.ui.activities.SignInActivity;
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
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by alan Lam on 31/7/16.
 */
public class StripeBaseActivityNew extends AppCompatActivity {

    // Animation
    Animation slide_down;
    Animation slide_up;
    // ----------Progress Bar
    private Timer timer;
    public View view;

    static String PRIVATE_KEY;
    //private NumberProgressBar bnp;

    private String redirect_from;
    private String name = "";
    private String package_name = "";
    private String total = "";
    private String package_id = "";
    private String txn_id = "";
    private String userid;
    // VARIABLE
    private String mLastInput;
    private String mShopName = "PosFone";
    private String image_url = "https://picsum.photos/200/300/?random";
    private String mDescription = "Package Subscription";
    private String mCurrency = "GBP";
    private String mEmail = "";

    private static boolean shouldAllowBack = true;


    //Ui----------------------------
    public CoordinatorLayout relativeLayout;
    @BindView(R.id.stripe_dialog_card_container)
    public LinearLayout mStripe_dialog_card_container;
    @BindView(R.id.stripe_dialog_date_container)
    public LinearLayout mStripe_dialog_date_container;
    @BindView(R.id.stripe_dialog_cvc_container)
    public LinearLayout mStripe_dialog_cvc_container;
    @BindView(R.id.stripe_dialog_email_container)
    public LinearLayout mStripe_dialog_email_container;
    @BindView(R.id.stripe_dialog_card)
    public EditText mCreditCard;
    @BindView(R.id.stripe_dialog_date)
    public EditText mExpiryDate;
    @BindView(R.id.stripe_dialog_cvc)
    public EditText mCVC;
    @BindView(R.id.stripe_dialog_card_icon)
    public ImageView mStripeDialogCardIcon;
    @BindView(R.id.stripe_dialog_txt1)
    public TextView mTitleTextView;
    @BindView(R.id.stripe_dialog_txt2)
    public TextView mDescriptionTextView;
    @BindView(R.id.stripe_dialog_error)
    public TextView mErrorMessage;
    @BindView(R.id.txt_plan_type)
    public TextView packageName;
    @BindView(R.id.btn_upgrade_plan)
    public TextView selected;
    @BindView(R.id.txt_plan_exire_date)
    public TextView pay_number;
    @BindView(R.id.stripe_dialog_email)
    public TextView mEmailTextView;
    @BindView(R.id.text_card_last_digits)
    public TextView text_card_last_digits;

    // @BindView()
    public CircleImageView mShopImageView;
    @BindView(R.id.stripe_dialog_paybutton)
    public Button mStripe_dialog_paybutton;
    public StripeImageView mExitButton;
    //  @BindView()
    public ProgressDialog dialog;
    @BindView(R.id.top_show_hide)
    LinearLayout top_show_hide;
    @BindView(R.id.current_card)
    LinearLayout current_card;
    @BindView(R.id.add_card_view)
    CardView add_card_view;
    @BindView(R.id.below)
    FrameLayout add_card_creadential;

    @BindView(R.id.view_card_update_success)
    public View view_card_update_success;
    @BindView(R.id.text_btn_continuew)
    public TextView text_btn_continuew;

    @BindView(R.id.card_form_view)
    public View card_form_view;


    //-------------------
    public Stripe mStripe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stripe__dialog_);
        ButterKnife.bind(this);
        // Progress Bar
        //bnp = (NumberProgressBar) findViewById(R.id.number_progress_bar);
        timer = new Timer();
        //bnp.setOnProgressBarListener(this);
        // Getting package Details
        redirect_from = getIntent().getStringExtra("redirect_from");
        package_id = getIntent().getStringExtra("package_id");
        package_name = getIntent().getStringExtra("package_name");
        txn_id = getIntent().getStringExtra("txn_id");
        total = getIntent().getStringExtra("total");
        //widgets-----------------
        add_card_creadential.setVisibility(View.INVISIBLE);
        relativeLayout = findViewById(R.id.pay_layout);
        //-----------------------------
        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        text_card();
        get_private_key();
        get_saved_Card();
        //showProgress();
        if (redirect_from != null && redirect_from.equalsIgnoreCase("add_new_card")) {
            showPageForAddNewCard();
        }

    }


    private void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait till process completes");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void stopProgress() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    //on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }


    @OnClick(R.id.current_card)
    public void anim() {
        // Start animation
        add_card_creadential.clearAnimation();
        top_show_hide.setVisibility(View.VISIBLE);
        add_card_creadential.setVisibility(View.GONE);
        // top_show_hide.startAnimation(saved_card_anim && top_show_hide.getAnimation() !=null ?slide_down:slide_up);
        if (top_show_hide.getAnimation() == null)
            top_show_hide.startAnimation(slide_down);

    }

    @OnClick(R.id.add_card_view)
    public void show_anim() {

        // Start animation
        top_show_hide.clearAnimation();
        top_show_hide.setVisibility(View.GONE);
        add_card_creadential.setVisibility(View.VISIBLE);
        //  add_card_creadential.startAnimation(new_card_anim && add_card_creadential.getAnimation()!=null ?slide_up:slide_down);
        if (add_card_creadential.getAnimation() == null)
            add_card_creadential.startAnimation(slide_down);
    }

    // Test Card---------------------------------------
    private void text_card() {
        try {
            mStripe = new Stripe(this, ApiClient.mDefaultPublishKey);
        } catch (Exception e) {

        }
        SharedPreferenceHandler sharedPreferenceHandler = new SharedPreferenceHandler(this);
        name = sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USERNAME);
        String number = sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_NEW_PAY_729_NUMBER);
        userid = sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);
        mEmail = sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_USER_EMAIL);
        String number_retain = sharedPreferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_PROFILE_PAY_729_NUMBER);
        //mShopImageView.setUrl(image_url);
        mTitleTextView.setText(mShopName);
        pay_number.setText(package_name);
        if (number != null) {
            selected.setText(number);
            //sharedPreferenceHandler.putValue(SharedPreferenceHandler.SP_KEY_NEW_PAY_729_NUMBER,null);
        } else
            selected.setText(number_retain);
        mDescriptionTextView.setText(mDescription);
        mStripe_dialog_paybutton.setText(getString(R.string.__stripe_pay) + " " + "\u00a3 " + (total));
        mStripe_dialog_paybutton.setOnClickListener(mPayClickListener);
        if (mEmail != null && mEmail.length() > 0) {
            mEmailTextView.setText(mEmail);
            mStripe_dialog_email_container.setVisibility(View.VISIBLE);
        }

        mExpiryDate.addTextChangedListener(mCreditCardExpireDateTextWatcher);
        mCreditCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mStripe_dialog_card_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_top);
                } else {
                    mStripe_dialog_card_container.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
        mExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mStripe_dialog_date_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_left_bottom);
                } else {
                    mStripe_dialog_date_container.setBackgroundResource(android.R.color.transparent);
                }
            }
        });
        mCVC.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mStripe_dialog_cvc_container.setBackgroundResource(R.drawable.stripe_inputbox_background_selected_right_bottom);
                } else {
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
                if (mCreditCard.getText().length() > 0) {
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
                } else {
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
                    } else {
                        mExpiryDate.setText(mExpiryDate.getText().toString().substring(0, 1));
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
            if (mCreditCard.getText().toString().length() <= 0) {
                //anim();
                mCreditCard.setError(getString(R.string.__stripe_invalidate_card_number));
                return;
            }
            if (mCVC.getText().toString().length() <= 0) {
                //anim();
                mCVC.setError(getString(R.string.__stripe_invalidate_cvc));
                return;
            }
            if (mExpiryDate.getText().toString().length() <= 0) {
                //anim();
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
            if (mmCard.validateCard()) {
                mStripe_dialog_paybutton.setEnabled(false);
                mStripe_dialog_paybutton.setText("Please Wait ...");
                mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.color_phone_number));
                shouldAllowBack = false;
                showProgress();
                //bnp.setVisibility(View.VISIBLE);
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                bnp.incrementProgressBy(1);
//                            }
//                        });
//                    }
//                }, 2000, 100);

                mStripe.createToken(mmCard, PRIVATE_KEY, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        stopProgress();
                        if (error != null && error.getMessage().length() > 0) {
                            mErrorMessage.setText(error.getLocalizedMessage());
                            mErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onSuccess(Token token) {
                        stopProgress();

                        //String cvc = mCVC.getText().toString();

                        System.out.println("Id  " + token.getId() + "  account " + token.getBankAccount() + "  created  " + token.getCard() + token.getCreated() + "  used " + token.getUsed());

//                        boolean isDebitSuccess = StripePaymentService.debit(token,total,txn_id,package_name,package_id,name,selected.getText().toString());
//                        if(isDebitSuccess)
//                        {
//                            mStripe_dialog_paybutton.setText("Succesful Transaction");
//                            mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.color_light_green));
//                            System.out.println("Transaction Successful!");
//                            //purchaseTrialPakage();
//                        }
//                        else
//                        {
//                            mStripe_dialog_paybutton.setText("Transaction Failed");
//                            mStripe_dialog_paybutton.setBackgroundColor(getResources().getColor(R.color.error_red));
//                            bnp.setVisibility(View.INVISIBLE);
//
//                            System.out.println("Unsuccesful TRansaction ");
//                        }

                        //Send Token ID To Server
                        //Code here
                        Gson gson = new Gson();
                        String json = gson.toJson(token);
                        System.out.println("token id -> " + token.getId() + "  ->   " + package_id + "  json-> " + json);
                        shouldAllowBack = true;
                        if (redirect_from != null && redirect_from.equalsIgnoreCase("add_new_card")) {
                            updateCard(token.getId());
                        }else
                        {
                            purchaseTrialPakage(token.getId());
                        }


                        //   initiate_payment(token, package_id);

                    }
                });
            } else if (!mmCard.validateNumber()) {
                //anim();
                mCreditCard.setError(getString(R.string.__stripe_invalidate_card_number));
            } else if (!mmCard.validateExpiryDate()) {
                //anim();
                mExpiryDate.setError(getString(R.string.__stripe_invalidate_expirydate));
            } else if (!mmCard.validateCVC()) {
                //anim();
                mCVC.setError(getString(R.string.__stripe_invalidate_cvc));
            } else {
                //anim();
                mErrorMessage.setText(R.string.__stripe_invalidate_card_detail);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    };

    /*  Get private key for payment */
    private void get_private_key() {
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        String body = "json=" + "";
        Call call = RESTClient.call_GET(RESTClient.GET_PRIVATE_PAYMENT_KEY, header, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
                Log.i("onFailure", "onFailure");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println();
                        JSONObject jsonObject = new JSONObject(res);
                        PRIVATE_KEY = jsonObject.getString("key");
                        Log.i("onResponse stripe", PRIVATE_KEY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    //--------------------------Purchase package

    private void purchaseTrialPakage(String tokenId) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this, null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        Log.i("userid", userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("package_id", package_id);
        jsonObject.addProperty("token", tokenId);
        String body = "json=" + jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER_PURCHASE, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
                Log.i("onFailure", "onFailure");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        Log.e("error response", res);
                        JSONObject jsonObject = new JSONObject(res);
                        final String message = "Transaction Succesfull!" + "\n Do you Wish to save Your Card";

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Intent intent = new Intent(StripeBaseActivityNew.this, ManageNumberActivity.class);
                                    // System.out.println(redirect_from);
                                    if ("profile_screen_positive_click".equals(redirect_from)) {
                                        Intent myintent = new Intent(StripeBaseActivityNew.this, SignInActivity.class);
                                        startActivity(myintent);
                                        finish();
                                    } else if ("profile_screen".equals(redirect_from)) {
                                        intent.putExtra("redirect_from", "profile_screen");
                                        startActivity(intent);
                                    } else
                                        startActivity(intent);


                                    /*CustomAlertDialog.showDialogSingleButton(StripeBaseActivity.this, message, new CustomAlertDialog.I_CustomAlertDialog() {
                                        @Override
                                        public void onPositiveClick() {

                                            Intent intent = new Intent(StripeBaseActivity.this, ManageNumberActivity.class);
                                            // System.out.println(redirect_from);
                                            if ("profile_screen_positive_click".equals(redirect_from)) {
                                                Intent myintent = new Intent(StripeBaseActivity.this, SignInActivity.class);
                                                startActivity(myintent);
                                                finish();
                                            } else if ("profile_screen".equals(redirect_from)) {
                                                intent.putExtra("redirect_from", "profile_screen");
                                                startActivity(intent);
                                            } else
                                                startActivity(intent);

                                        }

                                        @Override
                                        public void onNegativeClick() {

                                            Intent intent = new Intent(StripeBaseActivity.this, ManageNumberActivity.class);
                                            // System.out.println(redirect_from);
                                            if ("profile_screen_positive_click".equals(redirect_from)) {
                                                Intent myintent = new Intent(StripeBaseActivity.this, SignInActivity.class);
                                                startActivity(myintent);
                                                finish();
                                            } else if ("profile_screen".equals(redirect_from)) {
                                                intent.putExtra("redirect_from", "profile_screen");
                                                startActivity(intent);
                                            } else
                                                startActivity(intent);


                                        }
                                    });*/

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

    private void get_saved_Card() {
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        String body = "json=" + "";
        Call call = RESTClient.call_GET(RESTClient.GET_CARD_DETAILS, header, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
                Log.i("onFailure", "onFailure");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println();
                        JSONObject jsonObject = new JSONObject(res);
                        String success = jsonObject.getString("success").toString();
                        final String fourdigit = jsonObject.getString("fourdigit").toString();
                        if (success.equalsIgnoreCase("1")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text_card_last_digits.setText(fourdigit);
                                }
                            });
                        }
                        Log.i("get_saved_Card onResponse", res);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void updateCard(String tokenId) {

        //Show loading dialog
        GeneralUtil.showProgressDialog(this, null);

        SharedPreferenceHandler preferenceHandler = new SharedPreferenceHandler(this);
        String userID = preferenceHandler.getStringValue(SharedPreferenceHandler.SP_KEY_USER_ID);

        //Header
        HashMap<String, String> header = new HashMap<>();
        header.put("x-api-key", ApiClient.X_API_KEY);
        header.put("userid", userID);
        Log.i("userid", userID);
        //RequestBody
        JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("package_id", package_id);
        jsonObject.addProperty("token", tokenId);
        String body = "json=" + jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.UPDATE_CARD, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
                Log.i("onFailure", "onFailure");
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        Log.e("response", res);
                        JSONObject jsonObject = new JSONObject(res);
                        final String message = "Transaction Succesfull!" + "\n Do you Wish to save Your Card";

                        if (jsonObject.has("success") && jsonObject.getString("success").equalsIgnoreCase("1")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    card_form_view.setVisibility(View.GONE);
                                    view_card_update_success.setVisibility(View.VISIBLE);
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
            TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
            textView.setTextColor(getResources().getColor(R.color.cardview_light_background));
            // set background color
            toast.getView().setBackgroundColor(getResources().getColor(R.color.color_light_green));
        } else {
            super.onBackPressed();
        }
    }

    private void showPageForAddNewCard() {
        findViewById(R.id.top).setVisibility(View.GONE);
        findViewById(R.id.saved_card).setVisibility(View.GONE);
        findViewById(R.id.add_card_view).setVisibility(View.GONE);
        findViewById(R.id.below).setVisibility(View.VISIBLE);
        mStripe_dialog_paybutton.setText("SUBMIT");

        text_btn_continuew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
