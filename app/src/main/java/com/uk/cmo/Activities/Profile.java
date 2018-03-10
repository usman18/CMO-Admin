package com.uk.cmo.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {
    private ProgressBar profile_progressBar;
    private DatabaseReference reference;
    private CircleImageView pro_image;
    private TextView pro_name;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Bundle bundle=getIntent().getExtras();
        id=bundle.getString("UID");

        initialize();

    }

    private void initialize() {
        reference= FirebaseDatabase.getInstance().getReference("AllUsers");
        profile_progressBar=findViewById(R.id.profile_progressBar);
        pro_image=findViewById(R.id.pro_image);
        pro_name=findViewById(R.id.pro_name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference profile_ref=reference.child(id);

        profile_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person=dataSnapshot.getValue(Person.class);
                if(person!=null){
                    profile_progressBar.setVisibility(View.INVISIBLE);
                    make_visible();

                    Picasso.with(Profile.this)
                            .load(person.getProfile_pic())
                            .placeholder(R.drawable.profile)
                            .into(pro_image);

                    pro_name.setText(person.getName().trim());

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profile_progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void make_visible() {
        pro_image.setVisibility(View.VISIBLE);
        pro_name.setVisibility(View.VISIBLE);

    }
}
