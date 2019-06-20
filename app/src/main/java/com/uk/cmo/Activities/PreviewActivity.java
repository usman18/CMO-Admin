package com.uk.cmo.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;
import com.uk.cmo.Utility.Date;


public class PreviewActivity extends AppCompatActivity {

    private LinearLayout line;


    private TextView tvPersonName;
    private TextView tvDate;
    private ImageView imgProfilePic;
    private ImageView imgPostImage;
    private TextView tvPostDescription;

    private Button btnUpload;

    private String mPersonName;
    private String mProfilePic;
    private String mPostId;
    private String mUid;

    private String mDescription;
    private String mImage;

    private PostEntity postEntity = new PostEntity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            mPersonName = bundle.getString(AddPostActivity.PERSON_NAME);
            mPostId = bundle.getString(AddPostActivity.POST_ID);
            mProfilePic = bundle.getString(AddPostActivity.PRO_PIC);
            mUid = bundle.getString(AddPostActivity.UID);

            mDescription = bundle.getString(AddPostActivity.POST_DESCRIPTION);
            mImage = bundle.getString(AddPostActivity.POST_URI);

        }else {

            Toast.makeText(PreviewActivity.this,"There was some problem ! ",Toast.LENGTH_SHORT).show();
            finish();

        }

        initialize();


    }

    private void initialize() {

        tvPersonName = findViewById(R.id.post_name);
        imgProfilePic  =findViewById(R.id.post_profile_image);
        tvDate = findViewById(R.id.post_timestamp);
        imgPostImage = findViewById(R.id.post_img);
        tvPostDescription = findViewById(R.id.post_desc);

        btnUpload = findViewById(R.id.upload_button);

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mImage != null){
                    uploadPost();
                }else {
                    uploadNotice();
                }

            }
        });


        line = findViewById(R.id.line);

        tvPostDescription.setText(mDescription);
        tvPersonName.setText(mPersonName.trim());

        Glide.with(PreviewActivity.this)
                .load(mProfilePic)
                .apply(new RequestOptions().placeholder(R.drawable.profile))
                .into(imgProfilePic);

        tvDate.setText(Date.getDate(System.currentTimeMillis()));

        if (mImage != null){

            imgPostImage.setImageURI(Uri.parse(mImage));

        }else {

            imgPostImage.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);

        }



    }

    private void uploadNotice() {

        postEntity.setPost_type(PostEntity.NOTICE);

        postEntity.setUser_pp(mProfilePic);
        postEntity.setUser_name(mPersonName);
        postEntity.setTimeinmillis(System.currentTimeMillis() * (-1));

        //since its a notice
        postEntity.setPost_uri(null);

        postEntity.setDescription(mDescription);
        postEntity.setUid(mUid);
        postEntity.setPost_id(mPostId);

        saveToDb();


    }




    private void uploadPost() {

        postEntity.setPost_type(PostEntity.POST);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading...");
        dialog.show();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                FirebaseStorage.getInstance()
                        .getReference(Constants.POST_PICS)
                        .child(mUid)
                        .child(Uri.parse(mImage).getLastPathSegment() + System.currentTimeMillis())
                        .putFile(Uri.parse(mImage))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Uri download_uri = taskSnapshot.getDownloadUrl();

                                postEntity.setTimeinmillis(System.currentTimeMillis() * (-1));
                                postEntity.setDescription(mDescription.trim());

                                postEntity.setUser_name(mPersonName.trim());

                                postEntity.setUser_pp(mProfilePic);

                                postEntity.setUid(mUid.trim());

                                postEntity.setPost_id(mPostId.trim());

                                if (download_uri != null) {
                                    postEntity.setPost_uri(download_uri.toString());
                                    saveToDb();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                dialog.dismiss();


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


    private void saveToDb() {


        FirebaseDatabase.getInstance().getReference(Constants.POSTS)
                .child(mPostId)
                .setValue(postEntity);

        Toast.makeText(getApplicationContext(),"Posted Successfully!",Toast.LENGTH_LONG).show();

        Intent main_screen = new Intent(PreviewActivity.this,MainScreenActivity.class);
        main_screen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(main_screen);

    }




}
