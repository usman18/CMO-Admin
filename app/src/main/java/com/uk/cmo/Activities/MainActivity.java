package com.uk.cmo.Activities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Model.CreatedUser;
import com.uk.cmo.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean called=false;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ValueEventListener reference_listener;
    private Thread signinThread;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText email_id,password;
    private ProgressBar progressBar;
    private Button signin;
    private TextView signup;
    private FrameLayout frameLayout;
    private final int USER_DETAILS_ACTIVITY=1;
    private final int MAIN_SCREEN_ACTIVITY=3;
    private static int INTENT_RESULT = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        email_id=findViewById(R.id.email_id);
        password=findViewById(R.id.password_id);
        signin=findViewById(R.id.signin_button);
        signup=findViewById(R.id.tv_signup);
        signin.setOnClickListener(this);
        signup.setOnClickListener(this);
        frameLayout=findViewById(R.id.frame_layout);
        progressBar=findViewById(R.id.progressBar);

            ((ViewGroup) findViewById(R.id.root_layout)).getLayoutTransition()
                    .enableTransitionType(LayoutTransition.CHANGING);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                auth=FirebaseAuth.getInstance();
                firebaseUser=auth.getCurrentUser();

                if (firebaseUser!=null){
                    //User already logged in
                    BypassActivity();
                }else {
                    frameLayout.setVisibility(View.VISIBLE);
                }

            }
        },2000);



    }






    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.signin_button:
                //login method
                String email_id_text=email_id.getText().toString().trim();
                String pwd_text=password.getText().toString().trim();

                if(!TextUtils.isEmpty(email_id_text) && !TextUtils.isEmpty(pwd_text))
                    loginUser(email_id_text,pwd_text);
                else
                    Toast.makeText(getApplicationContext(),"Please fill in both the fields !",Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_signup:


                //intent to create account
                Intent intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                break;

        }
    }



    private void loginUser(final String email_id_text, final String pwd_text) {

        DisableWidgets();

        signinThread=new Thread(){

            @Override
            public void run() {
                super.run();
                    try {
                        signinThread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                auth.signInWithEmailAndPassword(email_id_text,pwd_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            Log.d("TAG","In Signin");
                                            firebaseUser = auth.getCurrentUser();
                                            BypassActivity();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        EnableWidgets();
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
            }

        };

        signinThread.start();

    }


    private void BypassActivity() {


        if(firebaseUser!=null) {

            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
            reference_listener = reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    CreatedUser createdUser;
                    createdUser = dataSnapshot.getValue(CreatedUser.class);
                    if(createdUser != null) {
                        if (createdUser.isAccountsetup()) {

                            INTENT_RESULT = MAIN_SCREEN_ACTIVITY;
                            Log.d("TAG", "MainScreen Intent");
                            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                            intent.putExtra("legit",createdUser.isLegit());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        } else if (!createdUser.isAccountsetup()) {

                            //details screen
                            INTENT_RESULT = USER_DETAILS_ACTIVITY;
                            Log.d("TAG", "Account Intent");
                            Intent intent = new Intent(MainActivity.this, AccountDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
//                    Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(getApplicationContext(),"In Couldn't Sign In ",Toast.LENGTH_SHORT).show();
        }
    }


    private void EnableWidgets() {
        email_id.setEnabled(true);
        password.setEnabled(true);
        signin.setEnabled(true);
//        signup.setEnabled(true);
    }

    public void DisableWidgets(){
        email_id.setEnabled(false);
        password.setEnabled(false);
        signin.setEnabled(false);
//        signup.setEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
//        if(auth !=null)
//            auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(auth !=null)
//            auth.removeAuthStateListener(authStateListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (signinThread != null) {

            signinThread.interrupt();
            signinThread = null;

        }

        if (reference != null) {
            reference.removeEventListener(reference_listener);
        }

//        if (auth != null) {
//            auth.removeAuthStateListener(authStateListener);
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(reference!=null){
            reference.removeEventListener(reference_listener);
        }

    }
}
