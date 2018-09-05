package com.uk.cmo.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uk.cmo.Model.Person;
import com.uk.cmo.Model.StudyingPerson;
import com.uk.cmo.Model.WorkingPerson;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

import static com.uk.cmo.Activities.AccountDetailsActivity.Address;
import static com.uk.cmo.Activities.AccountDetailsActivity.Blood_group;
import static com.uk.cmo.Activities.AccountDetailsActivity.Contact;
import static com.uk.cmo.Activities.AccountDetailsActivity.Email;
import static com.uk.cmo.Activities.AccountDetailsActivity.full_name;
import static com.uk.cmo.Activities.AccountDetailsActivity.isMarried;
import static com.uk.cmo.Activities.AccountDetailsActivity.profile_uri;

public class ProfessionalDetailsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private Thread data_thread, upload_thread;
    private RadioButton working,studying;
    private LinearLayout working_linear_layout,studying_linear_layout;
    private TextView message_textview;
    private FloatingActionButton submit;
    private EditText occupation,work_address,work_contact_num,work_email,qualifications;
    private EditText pursuing,student_qualification;
    private CheckBox checkBox;
    private Uri download_uri;
    public static Person person;

    String Occupation,Work_Address,Work_contact_num,Work_Email,Qualifications;
    String Pursuing,S_Qualifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details2_activity);
        getSupportActionBar().hide();



        setUpFirebaseInstances();
        setUpInstances();


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(checkBox.isChecked()){

                        Work_contact_num = Contact;
                        Work_Email = Email;
                        work_contact_num.setText(Work_contact_num);
                        work_email.setText(Work_Email);
                        work_contact_num.setEnabled(false);
                        work_email.setEnabled(false);

                    }else{

                        Work_contact_num=null;
                        Work_Email=null;
                        work_contact_num.setEnabled(true);
                        work_email.setEnabled(true);
                        work_contact_num.setText("");
                        work_email.setText("");

                    }
                }
        });




        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!working.isChecked() && !studying.isChecked())

                    Snackbar.make(findViewById(R.id.relative_details2_layout),"Please select one of the above options !",Snackbar.LENGTH_SHORT).show();

                else{

                    getProfessionalDetails();   //gets  professional details of user
                    setAllDetails();   // object of Person class is set up

                    if(!checkIfAnyValueIsNull()){

                        uploadProfilePic();    //first profile thread has to be called so it wil generate the download uri which will be later appended in data thread

                    }else

                        Toast.makeText(getApplicationContext(),"Please fill in the details ! ",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void setAllDetails() {

        //Will always be true, since this is admin app
        person.setAdmin(true);

        person.setName(full_name.trim());
        person.setName_lower_case(full_name.toLowerCase().trim());

        person.setEmail_id(Email);
        person.setContact_number(Contact);
        person.setBlood_group(Blood_group);
        person.setMarried(isMarried);

        person.setLegit(false); // by default everyone's legit is false

        if(working.isChecked()){

            WorkingPerson workingPerson = new WorkingPerson();
            workingPerson.setOccupation(Occupation);
            workingPerson.setWorkplace_Address(Work_Address);
            workingPerson.setWorkplace_emailId(Work_Email);
            workingPerson.setWorkplace_contact_num(Work_contact_num);
            workingPerson.setQualifications(Qualifications);
            person.setWorkingPerson(workingPerson);

        } else if(studying.isChecked()){

            StudyingPerson studyingPerson=new StudyingPerson();
            studyingPerson.setPursuing(Pursuing);
            studyingPerson.setQualification(S_Qualifications);
            person.setStudyingPerson(studyingPerson);

        }

        if(person.isMember()){

            person.setRelation(Address);                //we ll change the hint or maybe create a new edittext
            String member_id=databaseReference.push().getKey();
            person.setID(member_id);

        }else if(!person.isMember()){                     //that means its representative

            person.setAddress(Address);
            person.setID(firebaseAuth.getCurrentUser().getUid());

        }


        if(profile_uri!=null && download_uri!=null){

            person.setProfile_pic(download_uri.toString());

        }
    }

    private void setUpFirebaseInstances() {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }

    private void uploadProfilePic() {

        upload_thread =new Thread() {
            @Override
            public void run() {
                super.run();
                if(profile_uri != null) {
                  try {
                      upload_thread.sleep(250);
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              progressBar.setVisibility(View.VISIBLE);

                              StorageReference profile_Reference = storageReference.child("ProfilePics").child(firebaseAuth.getCurrentUser().getUid())
                                      .child(profile_uri.getLastPathSegment() + System.currentTimeMillis());
                              profile_Reference.putFile(profile_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                      download_uri = taskSnapshot.getDownloadUrl();
                                      progressBar.setVisibility(View.INVISIBLE);
                                      saveDataToDb();

                                  }
                              }).addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      progressBar.setVisibility(View.INVISIBLE);
                                      Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                  }
                              });
                          }
                      });
                  }catch (InterruptedException e){
                      e.printStackTrace();
                  }
                }
            }
        };



        if(profile_uri == null){
            saveDataToDb();
        }else {
            upload_thread.start();
        }

    }

    private void saveDataToDb() {
        data_thread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth != null){
                    try {
                        data_thread.sleep(500);
                        runOnUiThread(new Runnable() {
                           @Override
                            public void run() {

                               progressBar.setVisibility(View.VISIBLE);

                               if(profile_uri!=null && download_uri!=null){
                                   person.setProfile_pic(download_uri.toString());
                               }

                               if(person.isMember()){

                                   DatabaseReference member_ref=databaseReference.child("Members")
                                           .child(firebaseAuth.getCurrentUser().getUid());
                                   member_ref.child(person.getID()).setValue(person);

                                   DatabaseReference reference = databaseReference.child("AllUsers")
                                           .child(person.getID());
                                   reference.setValue(person);

                                   progressBar.setVisibility(View.INVISIBLE);
                                   //create Intent for members activity
                                   Toast.makeText(getApplicationContext(),"Appended in Members",Toast.LENGTH_LONG).show();
                                   updateUI();

                               }else if(!person.isMember()){


                                   DatabaseReference representative_ref=databaseReference.child(Constants.REPRESENTATIVES)
                                           .child(person.getID());
                                   representative_ref.setValue(person);

//                                   appendInAllUsersChild(person);

                                   setAccountCompleted();
                                   progressBar.setVisibility(View.INVISIBLE);

                                   updateUI();
                               }

                           }
                        });
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        data_thread.start();
    }


    private void updateUI() {

        Snackbar.make(findViewById(R.id.relative_details2_layout),"ProfileActivity Submitted ! ",
                Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent members_intent=new Intent(ProfessionalDetailsActivity.this, MainScreenActivity.class);
                members_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(members_intent);

            }
        },500);

    }

    private void setAccountCompleted() {

        DatabaseReference mark_ref = databaseReference.getRef()
                .child(Constants.USERS)
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("accountsetup");
        mark_ref.setValue(true);
        Log.d("Control : ","Marked ");
    }

    private boolean checkIfAnyValueIsNull() {

        if(working.isChecked()){
            if(TextUtils.isEmpty(Occupation) || TextUtils.isEmpty(Work_Address)
                    || TextUtils.isEmpty(Work_contact_num)
                    || TextUtils.isEmpty(Work_Email)
                    || TextUtils.isEmpty(Qualifications)
                    )
                return true;
            else
                return false;

        }else if(studying.isChecked()){
            if(TextUtils.isEmpty(Pursuing) || TextUtils.isEmpty(S_Qualifications))
                return true;
            else
                return false;
        }

        return true;
    }

    private void getProfessionalDetails() {

        if(working.isChecked()){

            Occupation = occupation.getText().toString().trim();
            Work_Address = work_address.getText().toString().trim();
            Work_contact_num = work_contact_num.getText().toString().trim();
            Work_Email = work_email.getText().toString().trim();
            Qualifications = qualifications.getText().toString().trim();

        }else if(studying.isChecked()){

            Pursuing = pursuing.getText().toString().trim();
            S_Qualifications = student_qualification.getText().toString().trim();

        }

    }

    private void setUpInstances() {
        working=findViewById(R.id.working_btn);
        studying=findViewById(R.id.studying_btn);
        working_linear_layout=findViewById(R.id.working_linear_layout);
        studying_linear_layout=findViewById(R.id.studying_linear_layout);
        message_textview=findViewById(R.id.display_message_id);
        submit=findViewById(R.id.submit_button);
        progressBar=findViewById(R.id.progress_representative);

        //working IDs
        occupation=findViewById(R.id.occupation);
        work_address=findViewById(R.id.workplace_address);
        work_contact_num=findViewById(R.id.work_contact);
        work_email=findViewById(R.id.worlplace_email);
        qualifications=findViewById(R.id.working_qualifications);
        checkBox=findViewById(R.id.same_as_previous_checkbox);
        //studying IDs
        pursuing=findViewById(R.id.studying_pursuing);
        student_qualification=findViewById(R.id.studying_qualifying);
        working.setOnCheckedChangeListener(this);
        studying.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b){
            switch (compoundButton.getId()){
                case R.id.working_btn:
                    working_linear_layout.setVisibility(View.VISIBLE);
                    studying_linear_layout.setVisibility(View.INVISIBLE);
                    message_textview.setVisibility(View.INVISIBLE);
                    break;
                case R.id.studying_btn:
                    studying_linear_layout.setVisibility(View.VISIBLE);
                    working_linear_layout.setVisibility(View.INVISIBLE);
                    message_textview.setVisibility(View.INVISIBLE);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(data_thread !=null){
            data_thread.interrupt();
            data_thread =null;
        }
        if(upload_thread !=null){
            upload_thread.interrupt();
            upload_thread =null;
        }
    }
}
