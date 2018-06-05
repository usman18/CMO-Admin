package com.uk.cmo.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class Profile extends AppCompatActivity {
    private ProgressBar profile_progressBar;
    private DatabaseReference reference;
    private ImageView pro_image;
    private TextView mail,number,address,marital_status,blood_group;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Bundle bundle=getIntent().getExtras();
        id=bundle.getString("UID");

        initialize();
        new FetchInfo().execute();

    }

    private void initialize() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
        collapsingToolbarLayout=findViewById(R.id.collapsing_toolabar_layout);
        reference= FirebaseDatabase.getInstance().getReference(Constants.ALLUSERS);


        pro_image=findViewById(R.id.pro_image);
        mail=findViewById(R.id.personal_mail);
        address=findViewById(R.id.personal_address);
        number=findViewById(R.id.personal_number);
        marital_status=findViewById(R.id.personal_marital_status);
        blood_group=findViewById(R.id.personal_blood_group);

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        DatabaseReference profile_ref=reference.child(id);
//
//        profile_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Person person=dataSnapshot.getValue(Person.class);
//                if(person!=null){
////                    profile_progressBar.setVisibility(View.INVISIBLE);
////                    make_visible();
//
//                    Glide.with(getApplicationContext())
//                            .load(person.getProfile_pic())
//                            .apply(new RequestOptions().placeholder(R.drawable.profile))
//                            .into(pro_image);
//
//                    collapsingToolbarLayout.setTitle(person.getName());
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
////                profile_progressBar.setVisibility(View.INVISIBLE);
//                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void make_visible() {
//        pro_image.setVisibility(View.VISIBLE);
//        pro_name.setVisibility(View.VISIBLE);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }




    public  class FetchInfo extends AsyncTask<Void,Void,Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            reference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Person person=dataSnapshot.getValue(Person.class);

                    collapsingToolbarLayout.setTitle(person.getName());

                    Glide.with(getApplicationContext())
                            .load(person.getProfile_pic())
                            .apply(new RequestOptions().placeholder(R.drawable.profile))
                            .into(pro_image);

                    mail.setText(person.getEmail_id());
                    number.setText(person.getContact_number());
                    address.setText(person.getAddress());

                    if (person.isMarried()){
                        marital_status.setText("Married");
                    }else {
                        marital_status.setText("Unmaried");
                    }

                    blood_group.setText(person.getBlood_group());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return null;
        }
    };
}
