package com.uk.cmo.Activities;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
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
    private TextView tv_marital_status;
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

    private RadioGroup radioGroup;
    private RadioButton rb_married;
    private RadioButton rb_unmarried;

    private Spinner sp_blood_group;

    //ETs for Professional Details
    private EditText et_pro_mail;
    private EditText et_pro_number;     //Todo : make input type as number
    private EditText et_pro_address;
    private EditText et_occupation;
    private EditText et_qualification;


    private ImageView img_edit_personal;
    private ImageView img_edit_professional;

    //Below two booleans will be used to control visibility of widgets
    private boolean personal_edit = false;
    private boolean professional_edit = false;

    private ImageView img_profile_image;

    private FirebaseAuth mAuth;

    private Person person;

   ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        initialize();
        getUserDetails();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize() {

        mAuth = FirebaseAuth.getInstance();

        ((ViewGroup) findViewById(R.id.root_layout))
                .getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dialog = new ProgressDialog(this);

        img_profile_image = findViewById(R.id.profile_pic);
        findViewById(R.id.camera_layout).setOnClickListener(this);

        //Personal Details widgets
        tv_mail = findViewById(R.id.personal_mail);
        tv_number = findViewById(R.id.personal_number);
        tv_address = findViewById(R.id.personal_address);
        tv_marital_status = findViewById(R.id.personal_marital_status);
        tv_blood_group = findViewById(R.id.personal_blood_group);

        et_mail = findViewById(R.id.et_personal_mail);
        et_number = findViewById(R.id.et_personal_number);
        et_address = findViewById(R.id.et_personal_address);

        radioGroup = findViewById(R.id.marital_rg);
        rb_married = findViewById(R.id.rb_married);
        rb_unmarried = findViewById(R.id.rb_unmarried);

        sp_blood_group = findViewById(R.id.sp_blood_group);

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
        et_qualification = findViewById(R.id.et_pro_quali);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.edit_personal_details:
                updatePersonalWidgets(personal_edit = !personal_edit);
                break;

            case R.id.edit_professional_details:
                updateProfessionalWidgets(professional_edit = !professional_edit);
                break;

            case R.id.camera_layout:
               // selectImage();
                showAlertDialog();

        }
    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);

        View view = LayoutInflater.from(MyProfileActivity.this)
                .inflate(R.layout.profilepic_options_dialog,null);


        builder.setView(view);

        LinearLayout update = view.findViewById(R.id.update);
        LinearLayout remove = view.findViewById(R.id.remove);

        final AlertDialog dialog = builder.create();


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectImage();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                removePicture();
            }
        });

        dialog.show();



    }


    private void selectImage() {

        CropImage.activity()
                .setAspectRatio(1,1)
                .setOutputCompressQuality(50)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(MyProfileActivity.this);

    }


    private void removePicture() {

        dialog.setMessage("Removing...");
        dialog.setCanceledOnTouchOutside(false);

        if (person.getProfile_pic() != null) {
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(person.getProfile_pic());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    dialog.show();
                    reference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    dialog.dismiss();
                                    person.setProfile_pic(null);
                                    updateDb("Profile Picture removed!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            "Could not update Profile Pic", Toast.LENGTH_SHORT)
                                            .show();

                                }
                            });

                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"You already don't have a profile pic !",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                img_profile_image.setImageURI(resultUri);
                updateStorage(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }



    }

    private void updateStorage(final Uri resultUri) {

        dialog.setMessage("Updating ...");
        dialog.setCanceledOnTouchOutside(false);

        if (person.getProfile_pic() != null) {
            final StorageReference reference = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(person.getProfile_pic());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    dialog.show();
                    reference.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                        updateProfilePic(resultUri);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),
                                            "Could not update Profile Pic", Toast.LENGTH_SHORT)
                                            .show();

                                }
                            });

                }
            });
        }else {
            dialog.show();
            updateProfilePic(resultUri);
        }

    }

    private void updateProfilePic(final Uri profilePic) {

        StorageReference reference
                = FirebaseStorage.getInstance().getReference(Constants.PROFILE_PICS)
                .child(mAuth.getCurrentUser().getUid())
                .child(profilePic.getLastPathSegment() + System.currentTimeMillis());

        reference.putFile(profilePic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        person.setProfile_pic(taskSnapshot.getDownloadUrl().toString());
                        updateDb("Profile Pic updated");
                        img_profile_image.setImageURI(profilePic);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Could not update Profile Pic",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

    }

    private void getUserDetails() {

        DatabaseReference reference =
                FirebaseDatabase.getInstance().getReference(Constants.REPRESENTATIVES)
                .child(mAuth.getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                person = dataSnapshot.getValue(Person.class);

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
                    tv_marital_status.setText("Married");
                }else {
                    tv_marital_status.setText("Unmarried");
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

                    //Occupation is being used to display pursuing for studying person
                    tv_occupation.setText(studyingPerson.getPursuing());
                    tv_quali.setText(studyingPerson.getQualification());

                    TextView tv = findViewById(R.id.text_occupation);
                    tv.setText("Pursuing : ");

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





    private void updatePersonalWidgets(boolean status) {

        if (status) {

            tv_mail.setVisibility(View.GONE);
            tv_number.setVisibility(View.GONE);
            tv_address.setVisibility(View.GONE);
            tv_marital_status.setVisibility(View.GONE);
            tv_blood_group.setVisibility(View.GONE);

            et_mail.setVisibility(View.VISIBLE);
            et_number.setVisibility(View.VISIBLE);
            et_address.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
            sp_blood_group.setVisibility(View.VISIBLE);

            et_mail.setText(tv_mail.getText().toString());
            et_number.setText(tv_number.getText().toString());
            et_address.setText(tv_address.getText().toString());

            if (person.isMarried()){
                rb_married.setChecked(true);
            }else {
                rb_unmarried.setChecked(true);
            }

            int position = Constants.blood_groups.indexOf(person.getBlood_group());

            if (position != -1){

                sp_blood_group.setSelection(position);

            }


            img_edit_personal.setBackground(getResources().getDrawable(R.drawable.ic_action_save));

        }else {

            tv_mail.setVisibility(View.VISIBLE);
            tv_number.setVisibility(View.VISIBLE);
            tv_address.setVisibility(View.VISIBLE);
            tv_marital_status.setVisibility(View.VISIBLE);
            tv_blood_group.setVisibility(View.VISIBLE);

            et_mail.setVisibility(View.GONE);
            et_number.setVisibility(View.GONE);
            et_address.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            sp_blood_group.setVisibility(View.GONE);

            //update data in person obj
            person.setEmail_id(et_mail.getText().toString());
            person.setContact_number(et_number.getText().toString());
            person.setAddress(et_address.getText().toString());

            if (rb_married.isChecked()) {
                person.setMarried(true);
            }else {
                person.setMarried(false);
            }

            person.setBlood_group(sp_blood_group.getSelectedItem().toString());

            //Updating UI accordingly
            tv_mail.setText(person.getEmail_id());
            tv_number.setText(person.getContact_number());
            tv_address.setText(person.getAddress());
            if (person.isMarried()){
                tv_marital_status.setText("Married");
            }else {
                tv_marital_status.setText("Unmarried");
            }
            tv_blood_group.setText(person.getBlood_group());

            img_edit_personal.setBackground(getResources().getDrawable(R.drawable.ic_action_edit));

            updateDb("Personal Details updated !");

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
            et_qualification.setVisibility(View.VISIBLE);

            et_pro_mail.setText(tv_pro_mail.getText().toString());
            et_pro_number.setText(tv_pro_number.getText().toString());
            et_pro_address.setText(tv_pro_address.getText().toString());
            et_qualification.setText(tv_quali.getText().toString());
            et_occupation.setText(tv_occupation.getText().toString());

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
            et_qualification.setVisibility(View.GONE);
            et_occupation.setVisibility(View.GONE);


            //updating person obj
            if (person.getWorkingPerson() != null) {
                //Person is at job or is working

                person.getWorkingPerson().setWorkplace_emailId(et_pro_mail.getText().toString());
                person.getWorkingPerson().setWorkplace_contact_num(et_pro_number.getText().toString());
                person.getWorkingPerson().setWorkplace_Address(et_pro_address.getText().toString());
                person.getWorkingPerson().setOccupation(et_occupation.getText().toString());
                person.getWorkingPerson().setQualifications(et_qualification.getText().toString());

            }else if (person.getStudyingPerson() != null) {
                //Person is pursuing something or is studying

                person.getStudyingPerson().setPursuing(et_occupation.getText().toString());
                person.getStudyingPerson().setQualification(et_qualification.getText().toString());

            }


            tv_pro_mail.setText(et_pro_mail.getText().toString());
            tv_pro_number.setText(et_pro_number.getText().toString());
            tv_pro_address.setText(et_pro_address.getText().toString());
            tv_quali.setText(et_qualification.getText().toString());
            tv_occupation.setText(et_occupation.getText().toString());

            img_edit_professional.setBackground(getResources().getDrawable(R.drawable.ic_action_edit));

            updateDb("Professional Details updated !");

        }
    }

    private void updateDb(String msg) {

        FirebaseDatabase.getInstance().getReference(Constants.REPRESENTATIVES)
                .child(mAuth.getCurrentUser().getUid())
                .setValue(person);

        Snackbar.make(findViewById(R.id.root_layout),msg,Snackbar.LENGTH_SHORT)
                .show();

    }

}
