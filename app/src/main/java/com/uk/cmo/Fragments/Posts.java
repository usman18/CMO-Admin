package com.uk.cmo.Fragments;

import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Activities.MyPostsActivity;
import com.uk.cmo.Adapters.PostAdapter;
import com.uk.cmo.Model.PostEntity;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by usman on 22-02-2018.
 */

public class Posts extends Fragment {
    private final int POSTS_PER_PAGE = 5;
    private FirebaseAuth firebaseAuth;
    private ProgressBar loading_progressbar;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearlayoutmanager;
    private  Query query;
    private ArrayList<PostEntity> postsArrayList = new ArrayList<>();
    private ArrayList<String> postIDs=new ArrayList<>();
    private PostAdapter adapter;
    private PostEntity last_entity;
    private boolean isScrolling = false;

    private String mUid;
    private boolean showAllPosts = true;    //default


    public static Posts getInstance() {
        return new Posts();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Args decide the behavior of Posts fragment, whether to show all posts or only this users posts
        Bundle args = getArguments();

        if (args != null){

            if (args.getString(MyPostsActivity.UID) != null){

                mUid = args.getString(MyPostsActivity.UID);
                showAllPosts = false;
                Log.d("Check","uid is " + mUid);

            }else {

                Log.d("Check","uid is null");

            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.posts, container, false);
    }

    // getChildCount = number of items visible
    // first visible item position = number of scrolled items
    // if visible items + scrolled out items == numberof posts_per pag

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);
        setUI();

    }


    private void initialize(View view) {

        reference = FirebaseDatabase.getInstance().getReference(Constants.POSTS);

        firebaseAuth = FirebaseAuth.getInstance();
        loading_progressbar = view.findViewById(R.id.loading_progressbar);
        loading_progressbar.setVisibility(View.VISIBLE);

        Log.d("Check ","rv Before Find View By ID");
        recyclerView = view.findViewById(R.id.post_recyclerview);
        mLinearlayoutmanager = new LinearLayoutManager(getActivity());
        mLinearlayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLinearlayoutmanager);
        adapter = new PostAdapter(getContext(), postsArrayList);
        recyclerView.setAdapter(adapter);

    }


    private void setUI() {

        if (showAllPosts) {

            recyclerView.addOnScrollListener(createListener());
            fetchAllPosts(0);

        }else if (mUid != null) {

            Log.d("Check","Bf fetch my posts");
            fetchMyPosts();

        }else {

            Toast.makeText(getContext(),"Something went wrong !", Toast.LENGTH_SHORT).show();
            getActivity().finish();

        }

    }


    private RecyclerView.OnScrollListener createListener() {


        return new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView1, int newState) {
                super.onScrollStateChanged(recyclerView1, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling =true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView1, int dx, int dy) {
                super.onScrolled(recyclerView1, dx, dy);

                int scrolled_items = mLinearlayoutmanager.findFirstVisibleItemPosition();
                int total_items = mLinearlayoutmanager.getItemCount();
                int visible = mLinearlayoutmanager.getChildCount();

                if (isScrolling && visible+scrolled_items == total_items) {
                    isScrolling = false;
                    Log.d("CHECKCALL:","FETCH DATA FIRED");

                    long millis = last_entity.getTimeinmillis();
                    fetchAllPosts(millis);

                }

            }
        };

    }



    public void fetchAllPosts(final long millis) {

        loading_progressbar.setVisibility(View.VISIBLE);
        Log.d("TAG","Data Fetching");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (millis == 0) {

                    Log.d("CHECKCALL","In if of fetch data");
                    query = reference.orderByChild("timeinmillis").limitToFirst(POSTS_PER_PAGE);

                } else {

                    Log.d("CHECKCALL","In else of fetch data");
                    query = reference.limitToFirst(POSTS_PER_PAGE).startAt(millis)
                            .orderByChild("timeinmillis");
                }
                query.keepSynced(true);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            PostEntity entity = snapshot.getValue(PostEntity.class);

                            if (!postIDs.contains(entity.getPost_id())) {

                                postIDs.add(entity.getPost_id());
                                postsArrayList.add(entity);
                                Log.d("CHECK: ", entity.getDescription());

                            }

                        }

                        loading_progressbar.setVisibility(View.GONE);

                        if (postsArrayList.size() != 0) {

                            last_entity = postsArrayList.get(postsArrayList.size() - 1);
                            adapter.notifyDataSetChanged();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        },1000);
    }

    public void fetchMyPosts() {

        loading_progressbar.setVisibility(View.VISIBLE);

        query = reference.orderByChild("uid")
                .equalTo(mUid);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Log.d("Check","In for loop");

                    PostEntity entity = snapshot.getValue(PostEntity.class);

                    if (!postIDs.contains(entity.getPost_id())) {

                        Log.d("Check","Post id is " + entity.getPost_id());
                        postIDs.add(entity.getPost_id());
                        postsArrayList.add(entity);

                    }

                }

                loading_progressbar.setVisibility(View.GONE);

                if (postsArrayList.size() != 0) {

                    Collections.reverse(postsArrayList);
                    loading_progressbar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}






