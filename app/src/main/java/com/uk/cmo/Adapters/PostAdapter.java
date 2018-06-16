package com.uk.cmo.Adapters;

import android.content.Context;
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
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.ybq.android.spinkit.SpinKitView;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Date;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by usman on 21-04-2018.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private ArrayList<PostEntity> postslist;

    public PostAdapter(Context context, ArrayList<PostEntity> postslist) {
        this.context = context;
        this.postslist = postslist;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row,parent,false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {
        final PostEntity entity=postslist.get(position);

        holder.setName(entity.getUser_name());

        holder.setImage(entity.getPost_uri(),entity.getUser_pp());

        holder.setDescription(entity.getDescription());
        Log.d("Adapter : ","Descrition : "+entity.getDescription());

        Log.d("Adapter : ", "ImageURI : "+entity.getPost_uri());

        long exact_millis=entity.getTimeinmillis()*(-1);

        holder.setTimestamp(exact_millis);



    }
    @Override
    public int getItemCount() {
        return postslist.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{

        TextView name,timestamp,description;
        ImageView post_image;
        CircleImageView profile_pic;
        SpinKitView progressBar;


        public PostViewHolder(View itemView) {
            super(itemView);

            progressBar=itemView.findViewById(R.id.post_progress);

        }

        void setName(String username){
            name=itemView.findViewById(R.id.post_name);
            name.setText(username);
        }

        void setImage(String imageURI,String profileURI){
            post_image=itemView.findViewById(R.id.post);
            profile_pic=itemView.findViewById(R.id.post_profile_image);
            if (imageURI==null){
                post_image.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }else if (imageURI!=null){
                Glide.with(context)
                        .load(imageURI)
                        .apply(new RequestOptions().placeholder(R.drawable.loading_placeholder))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(post_image);

            }

            Glide.with(context)
                    .load(profileURI)
                    .apply(new RequestOptions().placeholder(R.drawable.profile))
                    .into(profile_pic);

        }

        void setDescription(String postDespcription){

            description=itemView.findViewById(R.id.post_desc);
            description.setText(postDespcription);

        }

        void setTimestamp(long millis){

            timestamp=itemView.findViewById(R.id.post_timestamp);
            String date= Date.ToString(millis);
            timestamp.setText(date);

        }


    }
}
