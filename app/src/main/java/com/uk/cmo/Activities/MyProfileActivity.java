package com.uk.cmo.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.uk.cmo.R;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //TVs for Personal Details
    private TextView tv_mail;
    private TextView tv_number;
    private TextView tv_address;
    private TextView tv_marrital_status;
    private TextView tv_blood_group;

    //TVs for Professional Details
    private TextView tv_pro_mail;
    private TextView tv_pro_number;
    private TextView tv_pro_address;
    private TextView tv_occupation;
    private TextView tv_quali;

    //ETs for Personal Details
    private EditText et_mail;
    private EditText et_number;         // Todo : make input type as number
    private EditText et_address;

    //ETs for Professional Details
    private EditText et_pro_mail;
    private EditText et_pro_number;     //Todo : make input type as number
    private EditText et_pro_address;


    private ImageView img_edit_personal;
    private ImageView img_edit_professional;

    //Below two booleans will be used to control visibility of widgets
    private boolean personal_edit = false;
    private boolean professional_edit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Profile");
        }

        initialize();


    }

    private void initialize() {

        //Personal Details widgets
        tv_mail = findViewById(R.id.personal_mail);
        tv_number = findViewById(R.id.personal_number);
        tv_address = findViewById(R.id.personal_address);
        tv_marrital_status = findViewById(R.id.personal_marital_status);
        tv_blood_group = findViewById(R.id.personal_blood_group);

        et_mail = findViewById(R.id.et_personal_mail);
        et_number = findViewById(R.id.et_personal_number);
        et_address = findViewById(R.id.et_personal_address);


        img_edit_personal = findViewById(R.id.edit_personal_details);
        img_edit_professional = findViewById(R.id.edit_professional_details);

        img_edit_personal.setOnClickListener(this);
        img_edit_professional.setOnClickListener(this);

        //Professional Details Widgets
        tv_pro_mail = findViewById(R.id.tv_proffesional_email);
        tv_pro_number = findViewById(R.id.tv_proffesional_number);
        tv_pro_address = findViewById(R.id.tv_proffesional_address);
        tv_occupation = findViewById(R.id.tv_occupation_or_pursuing);
        tv_quali = findViewById(R.id.tv_qualification);

    }

    private void updatePersonalWidgets(boolean status) {

        if (status) {

            tv_mail.setVisibility(View.GONE);
            tv_number.setVisibility(View.GONE);
            tv_address.setVisibility(View.GONE);
            tv_marrital_status.setVisibility(View.GONE);
            tv_blood_group.setVisibility(View.GONE);

            et_mail.setVisibility(View.VISIBLE);
            et_number.setVisibility(View.VISIBLE);
            et_address.setVisibility(View.VISIBLE);

            img_edit_personal.setBackground(getResources().getDrawable(R.drawable.ic_action_save));

        }else {

            tv_mail.setVisibility(View.VISIBLE);
            tv_number.setVisibility(View.VISIBLE);
            tv_address.setVisibility(View.VISIBLE);
            tv_marrital_status.setVisibility(View.VISIBLE);
            tv_blood_group.setVisibility(View.VISIBLE);

            et_mail.setVisibility(View.GONE);
            et_number.setVisibility(View.GONE);
            et_address.setVisibility(View.GONE);

            img_edit_personal.setBackground(getResources().getDrawable(R.drawable.ic_action_edit));

        }

    }


    private void updateProfessionalWidgets(boolean status) {

        if (status) {

            tv_pro_mail.setVisibility(View.GONE);
            tv_pro_number.setVisibility(View.GONE);
            tv_pro_address.setVisibility(View.GONE);
            tv_occupation.setVisibility(View.GONE);
            tv_quali.setVisibility(View.GONE);

            et_pro_mail.setVisibility(View.VISIBLE);
            et_pro_number.setVisibility(View.VISIBLE);
            et_pro_address.setVisibility(View.VISIBLE);

            img_edit_professional.setBackground(getResources().getDrawable(R.drawable.ic_action_save));

        } else {

            tv_pro_mail.setVisibility(View.VISIBLE);
            tv_pro_number.setVisibility(View.VISIBLE);
            tv_pro_address.setVisibility(View.VISIBLE);
            tv_occupation.setVisibility(View.VISIBLE);
            tv_quali.setVisibility(View.VISIBLE);

            et_pro_mail.setVisibility(View.GONE);
            et_pro_number.setVisibility(View.GONE);
            et_pro_address.setVisibility(View.GONE);

            img_edit_professional.setBackground(getResources().getDrawable(R.drawable.ic_action_edit));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.edit_personal_details:
                updatePersonalWidgets(personal_edit = !personal_edit);
                break;

            case R.id.edit_professional_details:
                updateProfessionalWidgets(professional_edit = !professional_edit);
        }
    }
}
