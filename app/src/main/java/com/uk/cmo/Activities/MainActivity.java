package com.uk.cmo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private Button signin,signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!called){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            called=true;
        }

        auth =FirebaseAuth.getInstance();
        email_id=findViewById(R.id.email_id);
        password=findViewById(R.id.password_id);
        signin=findViewById(R.id.signin_button);
        signup=findViewById(R.id.sign_up_button);
        signin.setOnClickListener(this);
        signup.setOnClickListener(this);
        progressBar=findViewById(R.id.progressBar);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth!=null){
                    firebaseUser=auth.getCurrentUser();
                    BypassActivity();
                 }
            }
        };

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.signin_button:
                //login method
                String email_id_text=email_id.getText().toString().trim();
                String pwd_text=password.getText().toString().trim();

                if(!TextUtils.isEmpty(email_id_text) && !TextUtils.isEmpty(pwd_text))
                    Login(email_id_text,pwd_text);
                else
                    Toast.makeText(getApplicationContext(),"Please fill in both the fields !",Toast.LENGTH_LONG).show();
                break;
            case R.id.sign_up_button:
                //intent to create account
                Intent intent=new Intent(MainActivity.this,Create_Account.class);
                startActivity(intent);
                break;

        }
    }

    private void Login(final String email_id_text, final String pwd_text) {

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
                                            firebaseUser= auth.getCurrentUser();
                                            BypassActivity();
                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
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
            reference_listener=reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    CreatedUser createdUser=new CreatedUser();
                    createdUser = dataSnapshot.getValue(CreatedUser.class);
                    if(createdUser!=null) {
                        if (createdUser.isAccountsetup() && createdUser.isMembersetup()) {

                            Log.d("TAG", "MainScreen Intent");
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(MainActivity.this, MainScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else if (!createdUser.isAccountsetup()) {

                            //details screen
                            Log.d("TAG", "Account Intent");
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(MainActivity.this, Account_Details.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else if (createdUser.isAccountsetup() && !createdUser.isMembersetup()) {

                            //Todo : in each of the intents send the toast message as an extra to be displayed on the screen
                            Log.d("TAG", "Family Intent");
                            progressBar.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(MainActivity.this, FamilyMember.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
       //             Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void EnableWidgets() {
        email_id.setEnabled(true);
        password.setEnabled(true);
        signin.setEnabled(true);
        signup.setEnabled(true);
    }

    public void DisableWidgets(){
        email_id.setEnabled(false);
        password.setEnabled(false);
        signin.setEnabled(false);
        signup.setEnabled(false);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(auth !=null)
            auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(auth !=null)
            auth.removeAuthStateListener(authStateListener);

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

        if(auth !=null){
            auth.removeAuthStateListener(authStateListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(reference!=null){
            reference.removeEventListener(reference_listener);
        }

    }
}
