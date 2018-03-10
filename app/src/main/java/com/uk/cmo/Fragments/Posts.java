package com.uk.cmo.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.uk.cmo.Activities.AddPost;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.uk.cmo.Activities.MainActivity.called;

/**
 * Created by usman on 22-02-2018.
 */

public class Posts extends Fragment {
    private FirebaseAuth firebaseAuth;
    private Thread fetch;
    private DatabaseReference reference;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearlayoutmanager;
    private FloatingActionButton add_post;
    private Query query;
    public static Posts getInstance(){
        return new Posts();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.posts,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!called){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView=view.findViewById(R.id.post_recyclerview);
        add_post=view.findViewById(R.id.add_posts);

        recyclerView.setHasFixedSize(true);

        mLinearlayoutmanager=new LinearLayoutManager(getActivity());
        mLinearlayoutmanager.setReverseLayout(true);
        mLinearlayoutmanager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLinearlayoutmanager);

        reference= FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent add_post_intent=new Intent(getActivity(), AddPost.class);
                startActivity(add_post_intent);
            }
        });

        query=reference.child("Posts");
        query.keepSynced(true);
        FirebaseRecyclerOptions <PostEntity> options =new FirebaseRecyclerOptions.Builder<PostEntity>()
                .setQuery(query,PostEntity.class)
                .build();

        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<PostEntity,PostViewHolder>(options){

            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View inlated_post=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_row,parent,false);
                return new PostViewHolder(inlated_post,getContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostEntity model) {
                holder.name.setText(model.getUser_name());
                holder.description.setText(model.getDescription());
                holder.timestamp.setText(model.getTimestamp());

                FetchImages(holder,model);

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();

    }

    private void FetchImages(final PostViewHolder holder, final PostEntity entity){

        fetch=new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth!=null){
                    try {
                        fetch.sleep(50);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Picasso.with(getContext())
                                        .load(entity.getPost_uri())
                                        .placeholder(R.drawable.loading_placeholder)
                                        .into(holder.post_image, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                            }

                                            @Override
                                            public void onError() {
                                                holder.progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                Picasso.with(getContext())
                                        .load(entity.getUser_pp())
                                        .placeholder(R.drawable.profile)
                                        .into(holder.profile_pic);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };

        fetch.start();
    }



    @Override
    public void onStart() {
        super.onStart();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.stopListening();
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder{
        TextView name,timestamp,description;
        ImageView post_image;
        CircleImageView profile_pic;
        ProgressBar progressBar;


        public PostViewHolder(View itemView, Context context) {
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
