package com.uk.cmo.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class AddPostActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ImageView mPostImage;
    private EditText et_description;
    private Uri image_uri;
    private Uri download_uri;
    private Button btn_submit;
    private PostEntity postEntity;
    String uid,post_id;
    String name,pp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mFirebaseAuth = FirebaseAuth.getInstance();

        getUserDetails();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mPostImage = findViewById(R.id.post_image);
        et_description = findViewById(R.id.post_description);
        btn_submit = findViewById(R.id.submit_post);
        postEntity = new PostEntity();

        mPostImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddPostActivity.this);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_description.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Post atleast requires et_description !", Toast.LENGTH_SHORT).show();
                }else if (image_uri != null){

                    uploadPost();

                }else {
                    setPostObject();
                    postEntity.setPost_id(post_id);

                    DatabaseReference reference= mDatabaseReference.child("Posts")
                            .child(post_id);
                    reference.setValue(postEntity);

                    Toast.makeText(getApplicationContext(),"Posted Successfully!",Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent result=new Intent();
                            result.putExtra("result",true);
                            setResult(Activity.RESULT_OK,result);
                            finish();
                        }
                    },500);

                }
            }
        });

    }

    private void setPostObject() {


        postEntity.setTimeinmillis(System.currentTimeMillis()*(-1));    // storing in descending order, so latest post comes up
        postEntity.setDescription(et_description.getText().toString().trim());

        if (name != null) {
            postEntity.setUser_name(name);
        }else {
            Toast.makeText(getApplicationContext(),"User Name Not Found",Toast.LENGTH_LONG).show();
            finish();
        }

        postEntity.setUser_pp(pp);

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (uid != null){
            postEntity.setUid(uid);
        }else {
            finish();
            Toast.makeText(getApplicationContext(),"UID Not Found ",Toast.LENGTH_SHORT).show();
        }

        if (post_id != null){
            postEntity.setPost_id(post_id);
        }else {
            finish();       // if no post id, then simply finish
        }

        if(image_uri != null && download_uri != null) {

            postEntity.setPost_uri(download_uri.toString());
            postEntity.setPost_type(PostEntity.POST);

        }else {
            postEntity.setPost_type(PostEntity.NOTICE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                image_uri = result.getUri();
                mPostImage.setImageURI(image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void getUserDetails() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                FirebaseDatabase.getInstance()
                        .getReference(Constants.REPRESENTATIVES)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                name = dataSnapshot.child("name").getValue(String.class);
                                Log.d("Check",name);

                                pp = dataSnapshot.child("profile_pic").getValue(String.class);

                                post_id = FirebaseDatabase.getInstance().getReference().push().getKey();
                                Log.d("Check",post_id);

                                uid = mFirebaseAuth.getCurrentUser().getUid();


                                if (name == null ||  post_id == null || uid == null ){
                                    Toast.makeText(getApplicationContext(),"User info not found!",Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                Log.d("Check",uid);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext(),"Could not load details",Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                            }
                        });

            }
        });



    }



     private void uploadPost() {

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.show();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                FirebaseStorage.getInstance()
                        .getReference(Constants.POST_PICS)
                        .child(uid)
                        .child(image_uri.getLastPathSegment() + System.currentTimeMillis())
                        .putFile(image_uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                download_uri = taskSnapshot.getDownloadUrl();

                                postEntity.setTimeinmillis(System.currentTimeMillis() * (-1));
                                postEntity.setDescription(et_description.getText().toString().trim());

                                if (name != null) {
                                    postEntity.setUser_name(name);
                                }else {
                                    Toast.makeText(getApplicationContext(),"User Name Not Found",Toast.LENGTH_LONG).show();
                                    finish();
                                }

                                postEntity.setUser_pp(pp);

                                String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (uid!=null){
                                    postEntity.setUid(uid);
                                }else {
                                    finish();
                                    Toast.makeText(getApplicationContext(),"UID Not Found ",Toast.LENGTH_SHORT).show();
                                }

                                if (post_id!=null){
                                    postEntity.setPost_id(post_id);
                                }else {
                                    finish();
                                }

                                if(image_uri != null && download_uri != null) {

                                    postEntity.setPost_uri(download_uri.toString());
                                    postEntity.setPost_type(PostEntity.POST);

                                }else {
                                    postEntity.setPost_type(PostEntity.NOTICE);
                                }


                                FirebaseDatabase.getInstance().getReference(Constants.POSTS)
                                        .child(post_id)
                                        .setValue(postEntity);

                                dialog.dismiss();

                                Toast.makeText(getApplicationContext(),"Posted Successfully!",Toast.LENGTH_LONG).show();

                                finish();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Could not post",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }
                });



            }
        });

     }


}
