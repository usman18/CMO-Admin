package com.uk.cmo.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
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

        holder.name.setText(entity.getUser_name());
        holder.description.setText(entity.getDescription());

        long exact_millis=entity.getTimeinmillis()*(-1);
        String date= Date.ToString(exact_millis);
        holder.timestamp.setText(date);

        Glide.with(context)
                .load(entity.getPost_uri())
                .apply(new RequestOptions().placeholder(R.drawable.loading_placeholder))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.post_image);


//        Picasso.with(context)
//                .setIndicatorsEnabled(true);
//
//        Picasso.with(context)
//        .load(entity.getPost_uri())
//        .placeholder(R.drawable.loading_placeholder)
//        .fetch(new Callback() {
//            @Override
//            public void onSuccess() {
//                Picasso.with(context)
//                        .load(entity.getPost_uri())
//                        .into(holder.post_image);
//                holder.progressBar.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });

//        .into(holder.post_image, new Callback() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError() {
////                holder.progressBar.setVisibility(View.INVISIBLE);
//            }
//        });

        Glide.with(context)
                .load(entity.getUser_pp())
                .apply(new RequestOptions().placeholder(R.drawable.profile))
                .into(holder.profile_pic);

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

            name=itemView.findViewById(R.id.post_name);
            timestamp=itemView.findViewById(R.id.post_timestamp);
            description=itemView.findViewById(R.id.post_desc);
            post_image=itemView.findViewById(R.id.post);
            profile_pic=itemView.findViewById(R.id.post_profile_image);
            progressBar=itemView.findViewById(R.id.post_progress);

        }
    }
}
