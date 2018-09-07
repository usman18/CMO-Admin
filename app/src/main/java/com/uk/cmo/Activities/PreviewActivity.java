package com.uk.cmo.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uk.cmo.R;

public class PreviewActivity extends AppCompatActivity {

    private LinearLayout line;


    private ImageView mPostImage;
    private TextView mPostDescription;

    private String mDescription;
    private String mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            mDescription = bundle.getString(AddPostActivity.POST_DESCRIPTION);
            mImage = bundle.getString(AddPostActivity.POST_URI);

        }else {

            Toast.makeText(PreviewActivity.this,"There was some problem ! ",Toast.LENGTH_SHORT).show();
            finish();

        }

        initialize();


    }

    private void initialize() {

        mPostImage = findViewById(R.id.post_img);
        mPostDescription = findViewById(R.id.post_desc);

        line = findViewById(R.id.line);

        mPostDescription.setText(mDescription);

        if (mImage != null){

            mPostImage.setImageURI(Uri.parse(mImage));

        }else {

            mPostImage.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);

        }


    }
}
