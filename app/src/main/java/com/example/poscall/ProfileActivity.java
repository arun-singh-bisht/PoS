package com.example.poscall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
    }

    private void initViews()
    {
        //Back arrow
        findViewById(R.id.img_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.img_left:{
                finish();
            }
            break;
        }
    }
}
