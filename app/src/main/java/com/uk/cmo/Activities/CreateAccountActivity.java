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

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private Thread Create_Thread,SigninThread;
    private ProgressBar progressBar;
    private EditText email,pwd,full_name;
    private Button create;
    String name,Email,Pwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create__account);

//        if(!called){
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//            called=true;
//        }
        reference=FirebaseDatabase.getInstance().getReference("Users");
        firebaseAuth=FirebaseAuth.getInstance();
        email=findViewById(R.id.email_id);
        full_name=findViewById(R.id.representative_name_id);
        pwd=findViewById(R.id.password_id);
        progressBar=findViewById(R.id.create_progressBar);
        create=findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=full_name.getText().toString().trim();
                Email=email.getText().toString().trim();
                Pwd=pwd.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(Email) && !TextUtils.isEmpty(Pwd)) {
                    Create(name, Email, Pwd);
                }else {
                    Toast.makeText(getApplicationContext(),"Please fill in all the details ! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void Create(final String name, final String email, final String pwd) {
        Disable_Widgets();
        Create_Thread=new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth!=null){
                    try {
                        Create_Thread.sleep(500);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.VISIBLE);
                                firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            CreatedUser user=new CreatedUser(name,email,false,false);
                                            DatabaseReference databaseReference=reference.child(firebaseAuth.getCurrentUser().getUid());
                                            databaseReference.setValue(user);

                                            progressBar.setVisibility(View.INVISIBLE);
                                            SignIn_Thread(email,pwd);

                                        }else {

                                            firebaseAuth.createUserWithEmailAndPassword(email,pwd)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressBar.setVisibility(View.INVISIBLE);
                                                            Enable_Widgets();
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

        Create_Thread.start();
    }

    private void SignIn_Thread(final String email, final String pwd) {
       SigninThread=new Thread(){
           @Override
           public void run() {
               super.run();
               if(firebaseAuth!=null){
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

                                            Intent details_intent=new Intent(CreateAccountActivity.this,AccountDetailsActivity.class);
                                            details_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(details_intent);

                                        }else {

                                            progressBar.setVisibility(View.INVISIBLE);
                                            Enable_Widgets();
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

    private void Disable_Widgets() {
        email.setEnabled(false);
        full_name.setEnabled(false);
        pwd.setEnabled(false);
    }

    private void Enable_Widgets(){
        email.setEnabled(true);
        full_name.setEnabled(true);
        pwd.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(Create_Thread!=null){
            Create_Thread.interrupt();
            Create_Thread=null;
        }
        if(SigninThread!=null){
            SigninThread.interrupt();
            SigninThread=null;
        }
    }
}
