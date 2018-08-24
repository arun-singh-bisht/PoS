package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.posfone.promote.posfone.model.PackageModel;


public class PackageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private PackageModel packageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        initViews();
    }

    private void initViews()
    {

        packageModel = getIntent().getParcelableExtra("SelectedPackage");

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Package Detail");

        ((TextView)findViewById(R.id.txt_packageType)).setText(packageModel.package_name);
        ((TextView)findViewById(R.id.txt_packageRate)).setText(packageModel.recurring_total+" / ");
        ((TextView)findViewById(R.id.txt_packageDuration)).setText("Month");
        ((TextView)findViewById(R.id.txt_package_getway)).setText(packageModel.gateway_name);

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
                Bundle bundle = new Bundle();
                bundle.putParcelable("SelectedPackage",packageModel);
                intent.putExtras(bundle);
                startActivity(intent);

            }
            break;

        }
    }
}
