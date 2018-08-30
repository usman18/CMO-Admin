package com.uk.cmo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uk.cmo.Model.CreatedUser;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private Thread createThread,SigninThread;
    private ProgressBar progressBar;
    private EditText email,pwd,full_name;
    private Button create;
    String name,Email,Pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account);

        reference = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_id);
        full_name = findViewById(R.id.representative_name_id);
        pwd = findViewById(R.id.password_id);
        progressBar = findViewById(R.id.create_progressBar);
        create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = full_name.getText().toString().trim();
                Email = email.getText().toString().trim();
                Pwd = pwd.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Pwd)) {
                    createUser(name, Email, Pwd);
                }else {
                    Toast.makeText(getApplicationContext(),"Please fill in all the details ! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createUser(final String name, final String email, final String pwd) {

        disableWidgets();

        createThread = new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth != null){
                    try {
                        createThread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            String uid = firebaseAuth.getCurrentUser().getUid();

                                            CreatedUser user = new CreatedUser(name,email,uid,false);
                                            reference.child(uid).setValue(user);

                                            progressBar.setVisibility(View.INVISIBLE);
                                            signInUser(email,pwd);

                                        }else {

                                            firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            enableWidgets();
                                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                        }
                                    }
                                });
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        createThread.start();
    }

    private void signInUser(final String email, final String pwd) {
       SigninThread=new Thread(){
           @Override
           public void run() {
               super.run();
               if(firebaseAuth != null){
                   try {
                       SigninThread.sleep(500);
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               progressBar.setVisibility(View.VISIBLE);
                               firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                   @Override
                                   public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            progressBar.setVisibility(View.INVISIBLE);

                                            Intent details_intent=new Intent(SignUpActivity.this,AccountDetailsActivity.class);
                                            details_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(details_intent);

                                        }else {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            enableWidgets();
                                            Toast.makeText(getApplicationContext(),"Sign In Not Successful ! ",Toast.LENGTH_LONG).show();

                                        }
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
       SigninThread.start();
    }

    private void disableWidgets() {
        email.setEnabled(false);
        full_name.setEnabled(false);
        pwd.setEnabled(false);
    }

    private void enableWidgets(){
        email.setEnabled(true);
        full_name.setEnabled(true);
        pwd.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(createThread !=null){
            createThread.interrupt();
            createThread =null;
        }
        if(SigninThread!=null){
            SigninThread.interrupt();
            SigninThread=null;
        }
    }
}
