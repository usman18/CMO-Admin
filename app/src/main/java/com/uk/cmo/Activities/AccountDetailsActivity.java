package com.uk.cmo.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;

import static com.uk.cmo.Activities.ProfessionalDetailsActivity.person;

public class AccountDetailsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private EditText name,email,contact_num;
    private RadioButton married,unmarried;
    private TextView header_text;
    private Spinner bloodgroup_spinner;
    private EditText address;
    private ImageView profile_pic;
    private FloatingActionButton floatingActionButton;

    public static String full_name,Email,Contact,Blood_group,Address;
    public static Uri profile_uri;
    public static boolean isMarried;
    public static boolean familyMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__details);
        getSupportActionBar().hide();

        setUpInstances();

        if(familyMember){
            header_text.setText("Family Member details");
            address.setHint("Enter Relation with representative");
            person.setMember(true);
           familyMember = false;     //bydefault;
        }else {
            person.setMember(false);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fetch_Entries();
                if (!NullEntries()){

                    Intent details_2intent = new Intent(AccountDetailsActivity.this, ProfessionalDetailsActivity.class);
                    startActivity(details_2intent);

                }else {

                    Toast.makeText(getApplicationContext(),"Please fill in the entries ! ",Toast.LENGTH_LONG)
                            .show();

                }

            }
        });

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AccountDetailsActivity.this);
            }
        });


        married.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    unmarried.setChecked(false);
                }
            }
        });

        unmarried.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    married.setChecked(false);
                }
            }
        });

        bloodgroup_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void Fetch_Entries() {

        full_name=name.getText().toString().trim();

        Email=email.getText().toString().trim();
        Contact=contact_num.getText().toString().trim();
        //work on radio buttons

        if(married.isChecked() && !unmarried.isChecked())
            isMarried=true;
        else if(unmarried.isChecked() && !married.isChecked())
            isMarried=false;

        Blood_group=bloodgroup_spinner.getSelectedItem().toString().trim();
        Address=address.getText().toString().trim();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE ) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                profile_uri=resultUri;
                profile_pic.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean NullEntries() {
        //check whether all the details are not empty
        if(TextUtils.isEmpty(full_name) || TextUtils.isEmpty(Contact) || TextUtils.isEmpty(Email) ||
                (!married.isChecked() && !unmarried.isChecked())||
                TextUtils.isEmpty(Blood_group) || TextUtils.isEmpty(Address)
                )
            return true;
        else
            return false;
    }

    private void setUpInstances() {
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        contact_num=findViewById(R.id.number);
        married=findViewById(R.id.marriedradiobtn);
        unmarried=findViewById(R.id.unmarriedradiobtn);
        bloodgroup_spinner=findViewById(R.id.blood_group_spinner);
        address=findViewById(R.id.address);
        header_text=findViewById(R.id.person_text);
        profile_pic=findViewById(R.id.profile_image);
        floatingActionButton=findViewById(R.id.next);

        married.setOnCheckedChangeListener(this);
        unmarried.setOnCheckedChangeListener(this);
        person=new Person();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
}
