package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.posfone.promote.posfone.model.PackageModel;


public class PackageActivity extends AppCompatActivity implements View.OnClickListener {

    private PackageModel packageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package);

        initViews();
    }

    private void initViews()
    {

        packageModel = (PackageModel)getIntent().getParcelableExtra("SelectedPackage");

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Package");

        findViewById(R.id.img_right).setVisibility(View.GONE);
        findViewById(R.id.img_left).setOnClickListener(this);

        findViewById(R.id.lay_pakage_trial).setOnClickListener(this);
        findViewById(R.id.lay_pakage_regular).setOnClickListener(this);
        findViewById(R.id.lay_pakage_corporate).setOnClickListener(this);
        findViewById(R.id.lay_pakage_elite).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        String packageType = "";
        String packageRate = "";
        String packageDuration = "";
        String packageGatewayName = "";

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
            case R.id.lay_pakage_elite:
            {
                packageType = "Elite 234";
                packageRate = "$100.00";
                packageDuration = "Per Month";
                packageGatewayName = "WORLDPAY";
            }
            break;
            case R.id.lay_pakage_corporate:
            {
                    packageType = "Corporate";
                    packageRate = "$50.00";
                    packageDuration = "Per Month";
                    packageGatewayName = "GETWAY";
            }
            break;
            case R.id.lay_pakage_regular:
            {
                packageType = "Regular";
                packageRate = "$29.99";
                packageDuration = "Per Month";
                packageGatewayName = "GETWAY";
            }
            break;
            case R.id.lay_pakage_trial:{


                packageType = "Trial";
                packageRate = "Free";
                packageDuration = "7 Days Trial";
                packageGatewayName = "GETWAY";


            }
            break;
        }


        if(!packageType.isEmpty())
        {
            Intent intent = new Intent(PackageActivity.this,PackageDetailActivity.class);
            /*intent.putExtra("packageType",packageType);
            intent.putExtra("packageRate",packageRate);
            intent.putExtra("packageDuration",packageDuration);
            intent.putExtra("packageGatewayName",packageGatewayName);*/

            Bundle bundle = new Bundle();
            bundle.putParcelable("SelectedPackage",packageModel);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }
}
