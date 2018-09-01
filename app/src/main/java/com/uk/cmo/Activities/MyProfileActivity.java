package com.uk.cmo.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Model.Person;
import com.uk.cmo.Model.StudyingPerson;
import com.uk.cmo.Model.WorkingPerson;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

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
    private EditText et_occupation;
    private EditText et_quali;


    private ImageView img_edit_personal;
    private ImageView img_edit_professional;

    //Below two booleans will be used to control visibility of widgets
    private boolean personal_edit = false;
    private boolean professional_edit = false;

    private ImageView img_profile_image;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ActionBar actionBar = getSupportActionBar();

        //Todo : Add home / back button to action bar
        if (actionBar != null) {
            actionBar.setTitle("Profile");
        }

        initialize();
        getUserDetails();


    }

    private void getUserDetails() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference(Constants.REPRESENTATIVES)
                .child(mAuth.getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Person person = dataSnapshot.getValue(Person.class);

                if (person != null)
                    getSupportActionBar().setTitle(person.getName());


                Glide.with(getApplicationContext())
                        .load(person.getProfile_pic())
                        .apply(new RequestOptions().placeholder(R.drawable.profile))
                        .into(img_profile_image);

                tv_mail.setText(person.getEmail_id());
                tv_number.setText(person.getContact_number());
                tv_address.setText(person.getAddress());

                if (person.isMarried()){
                    tv_marrital_status.setText("Married");
                }else {
                    tv_marrital_status.setText("Unmaried");
                }

                tv_blood_group.setText(person.getBlood_group());

                if (person.getWorkingPerson() != null){

                    WorkingPerson workingPerson = person.getWorkingPerson();

                    findViewById(R.id.ll1).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll2).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll3).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll4).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll5).setVisibility(View.VISIBLE);
                    findViewById(R.id.ll6).setVisibility(View.VISIBLE);


                    tv_occupation.setText(workingPerson.getOccupation());
                    tv_quali.setText(workingPerson.getQualifications());
                    tv_pro_number.setText(workingPerson.getWorkplace_contact_num());
                    tv_pro_mail.setText(workingPerson.getWorkplace_emailId());
                    tv_pro_address.setText(workingPerson.getWorkplace_Address());


                }else if (person.getStudyingPerson() != null){

                    StudyingPerson studyingPerson = person.getStudyingPerson();
                    tv_occupation.setText(studyingPerson.getPursuing());
                    tv_quali.setText(studyingPerson.getQualification());

                    TextView tv = findViewById(R.id.text_occupation);
                    tv.setText("Pursuing");

                    findViewById(R.id.ll1).setVisibility(View.GONE);
                    findViewById(R.id.ll2).setVisibility(View.GONE);
                    findViewById(R.id.ll3).setVisibility(View.GONE);
                    findViewById(R.id.ll4).setVisibility(View.GONE);
                    findViewById(R.id.ll5).setVisibility(View.GONE);
                    findViewById(R.id.ll6).setVisibility(View.GONE);

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();

        img_profile_image = findViewById(R.id.profile_pic);

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

        et_pro_mail = findViewById(R.id.et_pro_mail);
        et_pro_number = findViewById(R.id.et_pro_number);
        et_pro_address = findViewById(R.id.et_pro_address);
        et_occupation = findViewById(R.id.et_pro_occ_pursuing);
        et_quali = findViewById(R.id.et_pro_quali);

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
            et_occupation.setVisibility(View.VISIBLE);
            et_quali.setVisibility(View.VISIBLE);

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
            et_quali.setVisibility(View.GONE);
            et_occupation.setVisibility(View.GONE);

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
