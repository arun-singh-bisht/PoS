package com.posfone.promote.posfone.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.posfone.promote.posfone.R;

import java.util.HashMap;


public class PackageDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String redirect_from;
    private String is_change_number;
    private String isTrial;
    private HashMap<String, String> packageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        initViews();

    }

    private void initViews() {
        redirect_from = getIntent().getStringExtra("redirect_from");
        is_change_number = getIntent().getStringExtra("is_change_number");
        isTrial =  getIntent().getStringExtra("isTrial");

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            packageModel = (HashMap<String, String>) bundle.getSerializable("SelectedPackage");
        }

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Package Detail");

        ((TextView) findViewById(R.id.txt_packageType)).setText(packageModel.get("Package Name"));
        ((TextView) findViewById(R.id.txt_packageRate)).setText("\u00a3" + packageModel.get("Subscription Charge") + " / ");
        ((TextView) findViewById(R.id.subscription_charge)).setText("Account Subscription Charge: " + "\u00a3" + packageModel.get("Subscription Charge"));
        ((TextView) findViewById(R.id.txt_packageDuration)).setText("Per Month");
        ((TextView) findViewById(R.id.txt_package_getway)).setText(packageModel.get("gatewayName"));

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.btn_purchase).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_left: {
                finish();
            }
            break;
            case R.id.btn_purchase: {

                if (is_change_number != null && is_change_number.equalsIgnoreCase("0")) {
                    Intent intent = new Intent(PackageDetailActivity.this, SummeryActivity.class);
                    Bundle extras = new Bundle();
                    intent.putExtra("redirect_from", redirect_from);
                    extras.putSerializable("SelectedPackage", packageModel);
                    extras.putSerializable("isTrial", isTrial);
                    intent.putExtras(extras);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PackageDetailActivity.this, ChooseNumberActivity.class);
                    Bundle extras = new Bundle();
                    intent.putExtra("redirect_from", redirect_from);
                    extras.putSerializable("SelectedPackage", packageModel);
                    extras.putSerializable("isTrial", isTrial);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            }
            break;

        }
    }
}
