package com.uk.cmo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.uk.cmo.Activities.ProfileActivity;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Date;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by usman on 21-04-2018.
 */

public class PostAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<PostEntity> postEntities;

    public PostAdapter(Context context, ArrayList<PostEntity> postEntities) {

        Log.d("RV ","In Adapter Constructor");
        this.context = context;
        this.postEntities = postEntities;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        Log.d("RV ",String.valueOf(viewType));
        switch (viewType){
            case PostEntity.POST:
                Log.d("RV ","In OnCreate PostViewHolder");
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_row,parent,false);
                return new PostViewHolder(view);
            case PostEntity.NOTICE:

                Log.d("RV ","In OnCreate NoticeViewHolder");
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notice,parent,false);
                return new NoticeViewHolder(view);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

        Log.d("RV ","In OnBind ViewHolder");

        final PostEntity entity=postEntities.get(position);

            switch (entity.getPost_type()){
                case PostEntity.POST:
                    ((PostViewHolder)holder).username.setText(entity.getUser_name());

                    long timeinmillis=entity.getTimeinmillis();
                    timeinmillis=(-1)*timeinmillis;
                    String date= Date.ToString(timeinmillis);

                    ((PostViewHolder)holder).timestamp.setText(date);

                        Glide.with(context)
                                .load(entity.getPost_uri().trim())
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        ((PostViewHolder) holder).postprogress.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        ((PostViewHolder) holder).postprogress.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).apply(new RequestOptions().placeholder(R.drawable.loading_placeholder))
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(((PostViewHolder) holder).postimage);


                    String pp_uri=entity.getUser_pp();

                    if (pp_uri!=null)
                        pp_uri=pp_uri.trim();

                    Glide.with(context)
                            .load(pp_uri)
                            .apply(new RequestOptions().placeholder(R.drawable.profile))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into((((PostViewHolder) holder).profile_pic));

                    ((PostViewHolder) holder).description.setText(entity.getDescription());

                    ((PostViewHolder) holder).username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadProfile(entity.getUid());
                        }
                    });

                    ((PostViewHolder) holder).profile_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadProfile(entity.getUid());
                        }
                    });

                    break;

                case PostEntity.NOTICE:
                    ((NoticeViewHolder)holder).username.setText(entity.getUser_name());

                    long millis=entity.getTimeinmillis();
                    millis = (-1) * millis;

                    Log.d("Time ","GivenTime " + String.valueOf(millis));
                    Log.d("Time ","SystemTime " + String.valueOf(System.currentTimeMillis()));
                    Log.d("Time","Diff" + String.valueOf(millis - System.currentTimeMillis()));

                    String time_stamp = Date.ToString(millis);

                    ((NoticeViewHolder) holder).timestamp.setText(time_stamp);

                    String pp_uri1 = entity.getUser_pp();

                    if (pp_uri1 != null)
                        pp_uri1 = pp_uri1.trim();

                    Glide.with(context)
                            .load(pp_uri1)
                            .apply(new RequestOptions().placeholder(R.drawable.profile))
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                            .into(((NoticeViewHolder) holder).profile_pic);

                    ((NoticeViewHolder) holder).description.setText(entity.getDescription());

                    ((NoticeViewHolder) holder).profile_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadProfile(entity.getUid());
                        }
                    });

                    ((NoticeViewHolder) holder).username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            loadProfile(entity.getUid());
                        }
                    });

                    break;
            }
    }

    @Override
    public int getItemCount() {

        Log.d("RV ","Returning size");

        return postEntities.size();
    }


    private void loadProfile(String uid) {

        Intent profile_intent = new Intent(context, ProfileActivity.class);
        profile_intent.putExtra("UID",uid);
        context.startActivity(profile_intent);
    }


    @Override
    public int getItemViewType(int position) {
        PostEntity postEntity=postEntities.get(position);

        switch (postEntity.getPost_type()){
            case PostEntity.POST:
                return PostEntity.POST;
            case PostEntity.NOTICE:
                return PostEntity.NOTICE;
            default:
                return -1;
        }

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView username,timestamp,description;
        CircleImageView profile_pic;
        ImageView postimage;
        SpinKitView postprogress;


        public PostViewHolder(View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.post_name);
            timestamp=itemView.findViewById(R.id.post_timestamp);
            description=itemView.findViewById(R.id.post_desc);
            profile_pic=itemView.findViewById(R.id.post_profile_image);
            postimage=itemView.findViewById(R.id.post);
            postprogress =itemView.findViewById(R.id.post_progress);

        }

    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder{

        public TextView username,timestamp,description;
        public CircleImageView profile_pic;


        public NoticeViewHolder(View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.post_name);
            timestamp=itemView.findViewById(R.id.post_timestamp);
            description=itemView.findViewById(R.id.post_desc);
            profile_pic=itemView.findViewById(R.id.post_profile_image);

        }
    }
}
