package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;


public class PackageDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String redirect_from;
    private HashMap<String,String> packageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        initViews();
        redirect_from=getIntent().getStringExtra("redirect_from");
    }

    private void initViews()
    {
        Bundle bundle = this.getIntent().getExtras();

        if(bundle != null) {
            packageModel = (HashMap<String, String>) bundle.getSerializable("SelectedPackage");
        }

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Package Detail");

        ((TextView)findViewById(R.id.txt_packageType)).setText(packageModel.get("Package Name"));
        ((TextView)findViewById(R.id.txt_packageRate)).setText("\u00a3"+packageModel.get("Subscription Charge")+" / ");
        ((TextView)findViewById(R.id.subscription_charge)).setText("Account Subscription Charge: "+"\u00a3"+packageModel.get("Subscription Charge"));
        ((TextView)findViewById(R.id.txt_packageDuration)).setText("Per Month");
        ((TextView)findViewById(R.id.txt_package_getway)).setText(packageModel.get("gatewayName"));

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);
        findViewById(R.id.btn_purchase).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
            case R.id.btn_purchase:{

                Intent intent = new Intent(PackageDetailActivity.this,SummeryActivity.class);
                Bundle extras = new Bundle();
                intent.putExtra("redirect_from",redirect_from);
                extras.putSerializable("SelectedPackage",packageModel);
                intent.putExtras(extras);
                startActivity(intent);

            }
            break;

        }
    }
}
