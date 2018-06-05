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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
//        setSupportActionBar(toolbar);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth=FirebaseAuth.getInstance();
        check_ref= FirebaseDatabase.getInstance().getReference(Constants.USERS);
        add_post = findViewById(R.id.add_posts);
        tabLayout=findViewById(R.id.tab_layout);
        viewPager=findViewById(R.id.view_pager);
        msg=findViewById(R.id.auth_msg);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        new Authentic().execute();


        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_post_intent = new Intent(MainScreenActivity.this, AddPost.class);
                startActivityForResult(add_post_intent,1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK)
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.getAdapter().notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.post_options,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                firebaseAuth.signOut();
                startActivity(new Intent(MainScreenActivity.this, MainActivity.class));
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

    class Authentic extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            check_ref.child(firebaseAuth.getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            CreatedUser user=dataSnapshot.getValue(CreatedUser.class);
                            if (user != null) {
                                if (!user.isLegit()){
                                    msg.setVisibility(View.VISIBLE);
                                }else {

                                    subscribeToPosts();
                                    viewPager.setAdapter(viewPagerAdapter);

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
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            Log.d("Boolean : ",String.valueOf(aBoolean));


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
