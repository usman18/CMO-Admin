package com.uk.cmo.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.uk.cmo.R;

public class MyProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Profile");
        }

        findViewById(R.id.edit_personal_details)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        findViewById(R.id.et_personal_mail)
                                .setVisibility(View.VISIBLE);
                        findViewById(R.id.personal_mail)
                                .setVisibility(View.GONE);
                    }
                });

    }
}
