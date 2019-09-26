package com.posfone.promote.posfone.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardInfo;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.JsonObject;
import com.posfone.promote.posfone.R;
import com.posfone.promote.posfone.Utils.GeneralUtil;
import com.posfone.promote.posfone.data.local.sp.SharedPreferenceHandler;
import com.posfone.promote.posfone.data.remote.rest.ApiClient;
import com.posfone.promote.posfone.data.remote.rest.RESTClient;
import com.posfone.promote.posfone.features.googlepay.GooglePay;
import com.posfone.promote.posfone.features.googlepay.GooglePayTest;
import com.posfone.promote.posfone.features.stripe.StripeBaseActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;


public class SummeryActivity extends AppCompatActivity implements View.OnClickListener {

    private HashMap<String, String> packageModel;
    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private String redirect_from;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;
    private String package_name = "";
    private String total = "";
    private String package_id = "";
    private String txn_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summery);
        redirect_from = getIntent().getStringExtra("redirect_from");
        initViews();

        // initialize a Google Pay API client for an environment suitable for testing
        mPaymentsClient =
                Wallet.getPaymentsClient(
                        this,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());

        //possiblyShowGooglePayButton();
        //isReadyToPay();
    }

    private void initViews() {

        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            packageModel = (HashMap<String, String>) bundle.getSerializable("SelectedPackage");
        }

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Summary");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);

        loadPackagePricingDetails();
    }

    private void loadPackagePricingDetails() {
        LinearLayout linearLayout = findViewById(R.id.packageDetailslayout);
        //Utils.getCurrencySymbol("INR");

        String key_order = packageModel.get("key_order");
        String[] key_order_array = key_order.split("\\*");
        for (int i = 0; i < key_order_array.length; i++) {
            key_order = key_order_array[i];

            View view = getLayoutInflater().inflate(R.layout.package_fee_item_row, null);
            TextView txt_item_name = view.findViewById(R.id.txt_item_name);
            TextView txt_item_value = view.findViewById(R.id.txt_item_value);

            txt_item_name.setText(key_order);
            String value = packageModel.get(key_order);
            if ("Package Name".equals(key_order)) {
                package_name = value;
                package_id = packageModel.get("packageId");
                txn_id = "12345";
            }
            if ("Grand Total".equals(key_order)) {
                total = value;
            }
            System.out.println("order is     " + package_name + " - " + total);

            if (value.isEmpty() || value.equalsIgnoreCase("null"))
                value = "\u00a3";
            if (i > 0)
                txt_item_value.setText("\u00a3" + value);
            else
                txt_item_value.setText(value);
            linearLayout.addView(view);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_left: {
                finish();
            }
            break;
            case R.id.btn_pay: {
                //Show Confirmation Dialog Box before confirming order
                System.out.println(package_id + "  ---  " + package_name);
                if ("Stripe Trail".equals(package_name)) {
                    Intent intent = new Intent(getApplicationContext(), ManageNumberActivity.class);
                    intent.putExtra("redirect_from", redirect_from);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    purchaseTrialPakage();
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), StripeBaseActivity.class);
                    //intent.putExtra("redirect_from", redirect_from);
                    intent.putExtra("redirect_from", redirect_from);
                    intent.putExtra("txn_id", txn_id);
                    intent.putExtra("package_id", package_id);
                    intent.putExtra("package_name", package_name);
                    intent.putExtra("total", total);
                    startActivity(intent);

                }
            }
            break;


        }
    }


    private void purchaseTrialPakage() {

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
        jsonObject.addProperty("package_id", packageModel.get("packageId"));
        //System.out.println("package id--------------- "+packageModel.get("packageId"));
        jsonObject.addProperty("txn_id", "00");
        String body = "json=" + jsonObject.toString();

        Call call = RESTClient.call_POST(RESTClient.TWILIO_NUMBER_PURCHASE, header, body, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                GeneralUtil.dismissProgressDialog();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) {

                GeneralUtil.dismissProgressDialog();

                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        Log.i("onResponse", res);
                        JSONObject jsonObject = new JSONObject(res);

                        if (jsonObject.has("status") && jsonObject.getString("status").equalsIgnoreCase("1")) {


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SummeryActivity.this, ManageNumberActivity.class);
                                    // System.out.println(redirect_from);
                                    if ("profile_screen_positive_click".equals(redirect_from)) {
                                        Intent myintent = new Intent(SummeryActivity.this, SignInActivity.class);
                                        startActivity(myintent);
                                        finish();
                                    } else if ("profile_screen".equals(redirect_from)) {
                                        intent.putExtra("redirect_from", "profile_screen");
                                        startActivity(intent);
                                    } else
                                        startActivity(intent);

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


    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button
     *
     * @see <a
     * href="https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    private void possiblyShowGooglePayButton() {
        final JSONObject isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (isReadyToPayJson == null) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        if (request == null) {
            return;
        }

        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result) {
                                // show Google as a payment option
                                mGooglePayButton = findViewById(R.id.googlepay);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestPayment(view);
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            }
                        } catch (ApiException exception) {
                            // handle developer errors
                        }
                    }
                });
    }

    private void isReadyToPay() {

        IsReadyToPayRequest request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build();
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    public void onComplete(Task<Boolean> task) {
                        try {
                            boolean result =
                                    task.getResult(ApiException.class);
                            if (result == true) {
                                //show Google as payment option
                                // show Google as a payment option
                                mGooglePayButton = findViewById(R.id.googlepay);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //requestPayment(view);
                                                PaymentDataRequest request = GooglePayTest.createPaymentDataRequest();
                                                if (request != null) {
                                                    AutoResolveHelper.resolveTask(
                                                            mPaymentsClient.loadPaymentData(request),
                                                            SummeryActivity.this,
                                                            LOAD_PAYMENT_DATA_REQUEST_CODE);
                                                    // LOAD_PAYMENT_DATA_REQUEST_CODE is a constant integer of your choice,
                                                    // similar to what you would use in startActivityForResult
                                                }
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);
                            } else {
                                //hide Google as payment option
                            }
                        } catch (ApiException exception) {
                        }
                    }
                });
    }


    /**
     * Display the Google Pay payment sheet after interaction with the Google Pay payment button
     *
     * @param view optionally uniquely identify the interactive element prompting for payment
     */
    public void requestPayment(View view) {
        JSONObject paymentDataRequestJson = GooglePay.getPaymentDataRequest();
        if (paymentDataRequestJson == null) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet
     *
     * @param requestCode the request code originally supplied to AutoResolveHelper in
     *                    requestPayment()
     * @param resultCode  the result code returned by the Google Pay API
     * @param data        an Intent from the Google Pay API containing payment or error data
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     * from an Activity</a>
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        //String json = paymentData.toJson();
                        // You can get some data on the user's card, such as the brand and last 4 digits
                        CardInfo info = paymentData.getCardInfo();
                        // You can also pull the user address from the PaymentData object.
                        UserAddress address = paymentData.getShippingAddress();
                        // This is the raw JSON string version of your Stripe token.
                        String rawToken = paymentData.getPaymentMethodToken().getToken();

                        // Now that you have a Stripe token object, charge that by using the id
                        //Token stripeToken = Token.fromString(rawToken);
                        //if (stripeToken != null) {
                        // This chargeToken function is a call to your own server, which should then connect
                        // to Stripe's API to finish the charge.
                        //chargeToken(stripeToken.getId());
                        //}
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        // Log the status for debugging.
                        // Generally, there is no need to show an error to the user.
                        // The Google Pay payment sheet will present any account errors.
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }
    }
}
