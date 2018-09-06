package com.uk.cmo.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.uk.cmo.Fragments.Posts;
import com.uk.cmo.R;

public class MyPostsActivity extends AppCompatActivity {

    public static final String UID = "my_uid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null){
            displayMessage();
        }


        FragmentManager fm = getSupportFragmentManager();
        Fragment mFragment = fm.findFragmentById(R.id.frame_layout);

        if (mFragment == null){

            //getting Posts Fragment instance
            mFragment = Posts.getInstance();

            //creating args for the fragment
            Bundle args = new Bundle();
            args.putString(UID,mUser.getUid());

            //setting args to fragment
            mFragment.setArguments(args);

            //setting the fragment to the container
            fm.beginTransaction()
                    .add(R.id.frame_layout,mFragment)
                    .commit();

        }

    }

    private void displayMessage() {

        Toast mToast = Toast.makeText(MyPostsActivity.this,"Could not load yout posts",Toast.LENGTH_SHORT);
        mToast.show();
    }
}
