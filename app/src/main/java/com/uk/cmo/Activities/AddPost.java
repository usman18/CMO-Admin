package com.uk.cmo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;

public class AddPost extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ImageView post_image;
    private EditText description;
    private Uri image_uri;
    private Uri download_uri;
    private Thread thread;
    private Button submit_button;
    private PostEntity postEntity;
    String post_id;
    String name,pp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        storageReference=FirebaseStorage.getInstance().getReference();
        post_image=findViewById(R.id.post_image);
        description=findViewById(R.id.post_description);
        progressDialog=new ProgressDialog(AddPost.this);
        submit_button=findViewById(R.id.submit_post);
        postEntity=new PostEntity();

        post_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddPost.this);
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(description.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Post atleast requires description !", Toast.LENGTH_SHORT).show();
                }else {
                    InstantiateObject();
                    postEntity.setPost_id(post_id);
                    DatabaseReference reference=databaseReference.child("Posts")
                            .child(post_id);
                    reference.setValue(postEntity);

                    Toast.makeText(getApplicationContext(),"Posted Successfully!",Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            finish();
                        }
                    },500);
                }
            }
        });

    }

    private void InstantiateObject() {


        postEntity.setTimeinmillis(System.currentTimeMillis()*(-1));
        postEntity.setDescription(description.getText().toString().trim());
        postEntity.setUser_name(name);
        postEntity.setUser_pp(pp);

        if(image_uri!=null && download_uri!=null)
            postEntity.setPost_uri(download_uri.toString());

    }

    @Override
    protected void onStart() {
        super.onStart();
        Fetch();

    }

    private void Fetch(){

        DatabaseReference fetchref=databaseReference.child("Representatives").child(firebaseAuth.getCurrentUser().getUid());
        fetchref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//                Person person=dataSnapshot.getValue(Person.class);
//                postEntity.setUser_name(person.getName().toString());
//                postEntity.setUser_pp(person.getProfile_pic().toString());
                name=dataSnapshot.child("name").getValue(String.class);
                pp=dataSnapshot.child("profile_pic").getValue(String.class);
                post_id=databaseReference.push().getKey();
                Toast.makeText(getApplicationContext(),name,Toast.LENGTH_SHORT).show();


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void Post() {
        thread=new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth!=null){
                    try {
                        thread.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(image_uri !=null) {

                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.setMessage("Uploading...");
                                    progressDialog.show();

                                    StorageReference reference=storageReference.child("PostPics").child(image_uri.getLastPathSegment());

                                    reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            download_uri=taskSnapshot.getDownloadUrl();
                                            progressDialog.dismiss();

                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                                if(image_uri==null)
                                    InstantiateObject();
                            }
                        });
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        };

        thread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                image_uri = result.getUri();
                post_image.setImageURI(image_uri);
                Post();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread!=null){
            thread.interrupt();
            thread=null;
        }
    }
}
