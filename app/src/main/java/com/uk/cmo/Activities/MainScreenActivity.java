package com.uk.cmo.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uk.cmo.Adapters.ViewPagerAdapter;
import com.uk.cmo.Model.CreatedUser;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class MainScreenActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference check_ref;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private android.support.v7.widget.Toolbar toolbar;
    private FloatingActionButton add_post;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.CustomTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firebaseAuth = FirebaseAuth.getInstance();
        check_ref = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        add_post = findViewById(R.id.add_posts);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        msg = findViewById(R.id.auth_msg);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);


//        if (!legit){
//            tabLayout.setVisibility(View.GONE);
//            msg.setVisibility(View.VISIBLE);
//            add_post.setVisibility(View.INVISIBLE);
//        }else {
//            subscribeToPosts();
//            viewPager.setAdapter(viewPagerAdapter);
//            msg.setVisibility(View.INVISIBLE);
//            add_post.setVisibility(View.VISIBLE);
//            InitializeToken();
//        }



        new Authentic().execute();
//
//        Toast.makeText(getApplicationContext(),"TimeStamp :" , ServerValue.TIMESTAMP);

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_post_intent = new Intent(MainScreenActivity.this, AddPostActivity.class);
                startActivity(add_post_intent);

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.POSTS);
                firebaseAuth.signOut();
                startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void InitializeToken() {

        String token= FirebaseInstanceId.getInstance().getToken();
        sendRegistrationTokenToServer(token);

    }

    private void sendRegistrationTokenToServer(String token){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser!=null) {
            Log.d("TAG", "user not null");
            reference.child(Constants.USERS).child(firebaseUser.getUid()).child(Constants.USERS_TOKEN).setValue(token);
        }else
            Log.d("TAG", "user null in method due to some reason");

    }


    private class Authentic extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            check_ref.child(firebaseAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CreatedUser user=dataSnapshot.getValue(CreatedUser.class);
                            if (user != null) {
                                if (!user.isLegit()){
                                    tabLayout.setVisibility(View.GONE);
                                    msg.setVisibility(View.VISIBLE);
                                    add_post.setVisibility(View.INVISIBLE);
                                }else {
                                    subscribeToPosts();
                                    viewPager.setAdapter(viewPagerAdapter);

                                    InitializeToken();
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private void subscribeToPosts(){

        FirebaseMessaging.getInstance().subscribeToTopic(Constants.POSTS);
        Log.d("Subscribed : ","This guy subscribed to Posts !");

    }




//    @Override
//    protected void onStart() {
//        super.onStart();
//        //Todo: use asynk task / thread for it
//        //Todo : Replace this code back to where it was in fragment
//        DatabaseReference check_ref=FirebaseDatabase.getInstance().getReference("Users")
//                .child(firebaseAuth.getCurrentUser().getUid());
//
//        check_ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                CreatedUser createdUser=dataSnapshot.getValue(CreatedUser.class);
//                if(createdUser!=null){
//                    if(createdUser.isLegit()){
//                        legit=true;
//                        //Enable Features
//                        //Disable Text and Progressbar
//             //           enableFeatures();
//                    }else {
//                        //Just Display the text and disable progress bar
//             //           disableFeatures();
//                        legit=false;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//    }

}
