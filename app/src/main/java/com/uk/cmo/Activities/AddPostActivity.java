package com.uk.cmo.Activities;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

public class AddPostActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private ImageView mPostImage;
    private EditText et_description;
    private Uri image_uri;
    private Uri download_uri;
//    private Button btn_submit;
    private Button btn_proceed;

    private TextView tv_select_img;

    private LinearLayout line1;
    private LinearLayout line2;

    private LinearLayout select_img;
    private FrameLayout img_layout;
    private ImageView img_dismiss;
    private ImageView img_edit;

    private PostEntity postEntity;
    String uid,post_id;
    String name,pp;

    public static final String PERSON_NAME = "name";
    public static final String PRO_PIC = "profile_pic";
    public static final String UID = "uid";
    public static final String POST_ID = "post_id";
    public static final String POST_URI = "post_uri";
    public static final String POST_DESCRIPTION = "post_description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        ((ViewGroup) findViewById(R.id.root_layout)).getLayoutTransition()
                .enableTransitionType(LayoutTransition.CHANGING);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mFirebaseAuth = FirebaseAuth.getInstance();

        getUserDetails();

        tv_select_img = findViewById(R.id.tv_select_img);
        img_edit = findViewById(R.id.img_edit);

        select_img = findViewById(R.id.select_img);

        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);

        img_layout = findViewById(R.id.image_layout);
        img_dismiss = findViewById(R.id.dismiss_image);

        mPostImage = findViewById(R.id.post_image);
        et_description = findViewById(R.id.post_description);

        btn_proceed = findViewById(R.id.proceed_button);

        postEntity = new PostEntity();

        select_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setOutputCompressQuality(50)
                        .start(AddPostActivity.this);
            }
        });

        img_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_layout.setVisibility(View.GONE);
                image_uri = null;

                line2.setVisibility(View.GONE);

                tv_select_img.setText(R.string.select_img);
                tv_select_img.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                img_edit.setBackground(getResources().getDrawable(R.drawable.ic_action_photo));
            }
        });

        et_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (TextUtils.isEmpty(s.toString())) {

                    btn_proceed.setBackgroundColor(Color.parseColor("#aeaeae"));

                } else {


                    btn_proceed.setBackgroundColor(Color.parseColor("#00bcd4"));

                }

            }
        });

        btn_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(et_description.getText().toString())) {

                    et_description.setError("Post needs at least a description");

                } else {

                    String mDescription = et_description.getText().toString();
                    String mImage = null;

                    if (image_uri != null) {
                        mImage = image_uri.toString();
                    }


                    Intent preview_intent = new Intent(AddPostActivity.this, PreviewActivity.class);

                    preview_intent.putExtra(PERSON_NAME, name);
                    preview_intent.putExtra(PRO_PIC, pp);
                    preview_intent.putExtra(UID, uid);
                    preview_intent.putExtra(POST_ID, post_id);
                    preview_intent.putExtra(POST_DESCRIPTION, mDescription);
                    preview_intent.putExtra(POST_URI, mImage);
                    startActivity(preview_intent);


                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                image_uri = result.getUri();
                mPostImage.setImageURI(image_uri);

                tv_select_img.setText("Image Selected");
                tv_select_img.setTextColor(Color.parseColor("#19790c"));

                img_edit.setBackground(getResources().getDrawable(R.drawable.ic_action_edit));

                line2.setVisibility(View.VISIBLE);

                img_layout.setVisibility(View.VISIBLE);


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


}
