package com.posfone.promote.posfone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class PackageDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        initViews();
    }

    private void initViews()
    {

        TextView txt_title = findViewById(R.id.txt_title);
        txt_title.setText("Package Detail");

        ((TextView)findViewById(R.id.txt_packageType)).setText(getIntent().getStringExtra("packageType").toString());
        ((TextView)findViewById(R.id.txt_packageRate)).setText(getIntent().getStringExtra("packageRate").toString()+" / ");
        ((TextView)findViewById(R.id.txt_packageDuration)).setText(getIntent().getStringExtra("packageDuration").toString());
        ((TextView)findViewById(R.id.txt_package_getway)).setText(getIntent().getStringExtra("packageGatewayName").toString());

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
                startActivity(new Intent(PackageDetailActivity.this,SummeryActivity.class));
            }
            break;

        }
    }
}
