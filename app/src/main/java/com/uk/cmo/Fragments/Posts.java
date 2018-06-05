package com.uk.cmo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Adapters.PostAdapter;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

import java.util.ArrayList;

/**
 * Created by usman on 22-02-2018.
 */

public class Posts extends Fragment {
    private final int POSTS_PER_PAGE = 4;
    private FirebaseAuth firebaseAuth;
    private SpinKitView loading_progressbar;
//    private TextView msg;
    private DatabaseReference reference;
    //    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearlayoutmanager;
//    private FloatingActionButton add_post;
    private ArrayList<PostEntity> postsArrayList = new ArrayList<>();
    private ArrayList<String> postIDs=new ArrayList<>();
    private PostAdapter adapter;
    private PostEntity last_entity;
    private boolean isscrolling=false;

    public static Posts getInstance() {
        return new Posts();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.posts, container, false);
        return view;
    }

    //TODO: Infinite Scroll getItemCount= number of items in adapter
    // getChildCount = number of items visible
    // first visible item position = number of scrolled items
    // if visible items + scrolled out items == numberof posts_per pag

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference(Constants.POSTS);
//        check_ref=FirebaseDatabase.getInstance().getReference(Constants.USERS);

        firebaseAuth = FirebaseAuth.getInstance();
        loading_progressbar=view.findViewById(R.id.loading_progressbar);
//        msg=view.findViewById(R.id.auth_msg);
        loading_progressbar.setVisibility(View.VISIBLE);
        recyclerView = view.findViewById(R.id.post_recyclerview);
//        add_post = view.findViewById(R.id.add_posts);
        recyclerView.setHasFixedSize(true);

        mLinearlayoutmanager = new LinearLayoutManager(getActivity());

//        mLinearlayoutmanager.setReverseLayout(true);
//        mLinearlayoutmanager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLinearlayoutmanager);
        adapter = new PostAdapter(getContext(), postsArrayList);
        recyclerView.setAdapter(adapter);
        FetchData(0);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isscrolling=true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrolled_items=mLinearlayoutmanager.findFirstVisibleItemPosition();
                int total_items = mLinearlayoutmanager.getItemCount();
                int visible=mLinearlayoutmanager.getChildCount();

//                Log.d(" : ",String.valueOf(visible_items));
//                Log.d("TAG : ",String.valueOf(scrolled_items));
             //   Log.d("TAG : ", String.valueOf(total_items));

                if (isscrolling && visible+scrolled_items==total_items) {
                    isscrolling=false;
                    Log.d("CHECKCALL:","FETCH DATA FIRED");
                    FetchData(last_entity.getTimeinmillis());
                }

            }
        });


    }



    public void FetchData(long millis) {
        Query query;
        loading_progressbar.setVisibility(View.VISIBLE);

        if (millis == 0) {
            Log.d("CHECKCALL","In if of fetch data");
            query = reference.orderByChild("timeinmillis").limitToFirst(POSTS_PER_PAGE);
        } else {
            Log.d("CHECKCALL","In else of fetch data");
            query = reference.limitToFirst(POSTS_PER_PAGE).startAt(millis);
            query = reference.limitToFirst(POSTS_PER_PAGE).startAt(millis)
                    .orderByChild("timeinmillis");
        }
        query.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    PostEntity entity=snapshot.getValue(PostEntity.class);
                    if (!postIDs.contains(entity.getPost_id())){
                        postIDs.add(entity.getPost_id());
                        postsArrayList.add(entity);
                        Log.d("CHECK: ",entity.getDescription());
                    }
                }

                if (postsArrayList.size()!=0)
                    last_entity=postsArrayList.get(postsArrayList.size()-1);

                loading_progressbar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.post_options,menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.sign_out:
//                firebaseAuth.signOut();
//                getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
//                getActivity().finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
}





//        enableFeatures();
//        query = reference.child("Posts");
//        query.keepSynced(true);
//        FirebaseRecyclerOptions<PostEntity> options = new FirebaseRecyclerOptions.Builder<PostEntity>()
//                .setQuery(query, PostEntity.class)
//                .build();
//
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<PostEntity, PostViewHolder>(options) {
//
//            @Override
//            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View inlated_post = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.post_row, parent, false);
//                return new PostViewHolder(inlated_post, getContext());
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostEntity model) {
//                holder.name.setText(model.getUser_name());
//                holder.description.setText(model.getDescription());
//                holder.timestamp.setText(model.getTimestamp());
//                FetchImages(holder, model);
//
//            }
//        };
//
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//        firebaseRecyclerAdapter.notifyDataSetChanged();
//
//    }
//
//
//    private void FetchImages(final PostViewHolder holder, final PostEntity entity){
//
//        fetch=new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                if(firebaseAuth!=null){
//                    try {
//                        fetch.sleep(50);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                                    Picasso.with(getContext())
//                                            .load(entity.getPost_uri())
//                                            .placeholder(R.drawable.loading_placeholder)
//                                            .into(holder.post_image, new Callback() {
//                                                @Override
//                                                public void onSuccess() {
//                                                    holder.progressBar.setVisibility(View.INVISIBLE);
//                                                }
//
//                                                @Override
//                                                public void onError() {
//                                                    holder.progressBar.setVisibility(View.INVISIBLE);
//                                                }
//                                            });
//                                Picasso.with(getContext())
//                                        .load(entity.getUser_pp())
//                                        .placeholder(R.drawable.profile)
//                                        .into(holder.profile_pic);
//                            }
//                        });
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//
//        fetch.start();
//    }

//    private void enableFeatures(){
//        auth_progressbar.setVisibility(View.INVISIBLE);
//        recyclerView.setVisibility(View.VISIBLE);
//        //Todo fab will be made visible in admin side
//    }
//
//    private void disableFeatures(){
//        auth_progressbar.setVisibility(View.INVISIBLE);
//        recyclerView.setVisibility(View.INVISIBLE);
//        auth_msg.setVisibility(View.VISIBLE);
//    }




//    public static class PostViewHolder extends RecyclerView.ViewHolder{
//        TextView name,timestamp,description;
//        ImageView post_image;
//        CircleImageView profile_pic;
//        ProgressBar progressBar;
//
//
//        public PostViewHolder(View itemView, Context context) {
//            super(itemView);
//
//            name=itemView.findViewById(R.id.post_name);
//            timestamp=itemView.findViewById(R.id.post_timestamp);
//            description=itemView.findViewById(R.id.post_desc);
//            post_image=itemView.findViewById(R.id.post);
//            profile_pic=itemView.findViewById(R.id.post_profile_image);
//            progressBar=itemView.findViewById(R.id.post_progress);
//        }
//
//    }

