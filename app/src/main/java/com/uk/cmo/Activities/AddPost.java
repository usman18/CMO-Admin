package com.uk.cmo.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

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
    String uid,post_id;
    String name,pp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Todo : Use Async Task To Fetch The User Details in onCreate
        //Todo : if not found finish the activity with a toast
        //Todo : even do not upload an image straight away
        //Todo : Upload when user taps submit
        progressDialog=new ProgressDialog(AddPost.this);
        new FetchUserDetails().execute();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);
        storageReference=FirebaseStorage.getInstance().getReference();
        post_image=findViewById(R.id.post_image);
        description=findViewById(R.id.post_description);
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
                }else if (image_uri!=null){

                    new UploadPost().execute();

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

    private void InstantiateObject() {


        postEntity.setTimeinmillis(System.currentTimeMillis()*(-1));
        postEntity.setDescription(description.getText().toString().trim());

        if (name!=null) {
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

        if(image_uri!=null && download_uri!=null) {
            postEntity.setPost_uri(download_uri.toString());
            postEntity.setPost_type(PostEntity.POST);
        }else {
            postEntity.setPost_type(PostEntity.NOTICE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Fetch();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                image_uri = result.getUri();
                post_image.setImageURI(image_uri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }


     class FetchUserDetails extends AsyncTask{

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             progressDialog.setMessage("Loading...");
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.show();
         }

         @Override
         protected Object doInBackground(Object[] objects) {

             final DatabaseReference reference=FirebaseDatabase.getInstance().getReference(Constants.REPRESENTATIVES)
                     .child(firebaseAuth.getCurrentUser().getUid());

             reference.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {


                     name=dataSnapshot.child("name").getValue(String.class);
                     Log.d("Check",name);
                     pp=dataSnapshot.child("profile_pic").getValue(String.class);

                     post_id = reference.push().getKey();
                     Log.d("Check",post_id);
                     uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                     if (uid == null) {
                         Toast.makeText(getApplicationContext(),"Could not post",Toast.LENGTH_SHORT).show();
                         finish();
                     }
                     Log.d("Check",uid);

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {


                 }
             });

             return null;
         }

         @Override
         protected void onPostExecute(Object o) {

             super.onPostExecute(o);

             progressDialog.dismiss();

             if (name!=null && pp!=null && post_id!=null && uid!=null ){
                 Toast.makeText(getApplicationContext(),"User info not found!",Toast.LENGTH_LONG).show();
                 Intent result=new Intent();
                 result.putExtra("result",false);
                 setResult(Activity.RESULT_OK,result);
                 finish();
             }
         }

     }

     class UploadPost extends AsyncTask<Void,Void,Void>{

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             progressDialog.setMessage("Uploading Post...");
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.show();

         }

         @Override
         protected Void doInBackground(Void... voids) {
             StorageReference post_storage=FirebaseStorage.getInstance().getReference("PostPics")
                     .child(uid)
                     .child(image_uri.getLastPathSegment()+System.currentTimeMillis());

             post_storage.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                     download_uri=taskSnapshot.getDownloadUrl();

                     postEntity.setTimeinmillis(System.currentTimeMillis()*(-1));
                     postEntity.setDescription(description.getText().toString().trim());

                     if (name!=null) {
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

                     if(image_uri!=null && download_uri!=null) {
                         postEntity.setPost_uri(download_uri.toString());
                         postEntity.setPost_type(PostEntity.POST);
                     }else {
                         postEntity.setPost_type(PostEntity.NOTICE);
                     }



                     DatabaseReference reference=FirebaseDatabase.getInstance()
                             .getReference(Constants.POSTS);

                     reference.child(post_id)
                             .setValue(postEntity);

                 }
             }).addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     e.printStackTrace();
                 }
             });

             return null;
         }

         @Override
         protected void onPostExecute(Void aVoid) {
             super.onPostExecute(aVoid);

             progressDialog.dismiss();
             if (postEntity.getPost_uri()!=null)
                Toast.makeText(getApplicationContext(),"Posted Successfully!",Toast.LENGTH_LONG).show();

             new Handler().postDelayed(new Runnable() {
                 @Override
                 public void run() {
                     Intent result=new Intent();
                     result.putExtra("result",true);
                     setResult(Activity.RESULT_OK,result);
                     finish();
                 }
             },1000);

         }
     }
}
