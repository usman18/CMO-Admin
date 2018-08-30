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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private ProgressBar loading_progressbar;
    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearlayoutmanager;
    private  Query query;
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

    // getChildCount = number of items visible
    // first visible item position = number of scrolled items
    // if visible items + scrolled out items == numberof posts_per pag

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reference = FirebaseDatabase.getInstance().getReference(Constants.POSTS);

        firebaseAuth = FirebaseAuth.getInstance();
        loading_progressbar = view.findViewById(R.id.loading_progressbar);
        loading_progressbar.setVisibility(View.VISIBLE);

        Log.d("RV ","Before Find View By ID");
        recyclerView = view.findViewById(R.id.post_recyclerview);
        recyclerView.setHasFixedSize(true);
        mLinearlayoutmanager = new LinearLayoutManager(getActivity());
        mLinearlayoutmanager.setOrientation(LinearLayoutManager.VERTICAL);

        FetchData(0);
        recyclerView.setLayoutManager(mLinearlayoutmanager);
        adapter = new PostAdapter(getContext(), postsArrayList);
        recyclerView.setAdapter(adapter);


        Log.d("RV ","After setting adapter");


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isscrolling=true;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int scrolled_items=mLinearlayoutmanager.findFirstVisibleItemPosition();
                int total_items = mLinearlayoutmanager.getItemCount();
                int visible=mLinearlayoutmanager.getChildCount();

                if (isscrolling && visible+scrolled_items==total_items) {
                    isscrolling=false;
                    Log.d("CHECKCALL:","FETCH DATA FIRED");
                    long millis=last_entity.getTimeinmillis();
                    FetchData(millis);

                }

            }
        });


    }





    public  void FetchData(final long millis) {

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


                        loading_progressbar.setVisibility(View.GONE);

                        if (postsArrayList.size()!=0) {
                            last_entity = postsArrayList.get(postsArrayList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            PostEntity entity=snapshot.getValue(PostEntity.class);
                            if (!postIDs.contains(entity.getPost_id())){
                                postIDs.add(entity.getPost_id());
                                postsArrayList.add(entity);
                                Log.d("CHECK: ",entity.getDescription());
                            }
                        }


                        loading_progressbar.setVisibility(View.GONE);

                        if (postsArrayList.size()!=0) {
                            last_entity = postsArrayList.get(postsArrayList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }


                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        },1000);
    }

}






