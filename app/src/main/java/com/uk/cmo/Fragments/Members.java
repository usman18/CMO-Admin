package com.uk.cmo.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uk.cmo.Activities.ProfileActivity;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

/**
 * Created by usman on 22-02-2018.
 */

public class Members extends Fragment {
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private SearchView searchView;
    private ImageView filter;
    private DatabaseReference reference;
    private Query query;
    private String choice = Constants.REPRESENTATIVES;

    public static Members getInstance(){
        return new Members();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.members,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize(view);

        setUpAdapter(query);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Todo : Think about how to show admins in members fragment
                // Todo : since multiple queries cannot be done.

                query = reference.child(choice).orderByChild(Constants.LOWERCASE_NAME)
                        .startAt(newText.toLowerCase().trim()).endAt(newText.toLowerCase().trim() + "\uf8ff");
                setUpAdapter(query);
                mFirebaseAdapter.startListening();

                return false;
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });


    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.filter_dialog,null,false);

        builder.setView(view);
        final Spinner filter_spinner = view.findViewById(R.id.filter_spinner);
        TextView apply = view.findViewById(R.id.apply_button);
        TextView cancel = view.findViewById(R.id.cancel_button);

        final AlertDialog alertDialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = filter_spinner.getSelectedItemPosition();

                if (position == 0){
                    choice = Constants.REPRESENTATIVES;
                }else if (position == 1) {
                    choice = Constants.ALLUSERS;
                }

                query = reference.child(choice).orderByChild(Constants.LOWERCASE_NAME);

                setUpAdapter(query);
                mFirebaseAdapter.startListening();
                alertDialog.dismiss();
            }
        });

        filter_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        alertDialog.show();
    }

    private void initialize(View view) {

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_fragment);
        filter = view.findViewById(R.id.filter);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        query = reference.child(choice).orderByChild(Constants.LOWERCASE_NAME);

    }


    private void setUpAdapter(Query query){


        final FirebaseRecyclerOptions<Person> options
                = new FirebaseRecyclerOptions.Builder<Person>()
                .setQuery(query,Person.class)
                .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Person,MemberViewHolder>(options){

            @Override
            public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.member_row,parent,false);
                return new MemberViewHolder(view,getContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull final Person model) {

                holder.name.setText(model.getName());

                if (model.getWorkingPerson() != null) {

                    holder.work.setText(model.getWorkingPerson().getOccupation());

                }else if (model.getStudyingPerson() != null) {

                    holder.work.setText("Pursuing : " + model.getStudyingPerson().getPursuing());
                }


                Glide.with(getContext())
                        .load(model.getProfile_pic())
                        .apply(new RequestOptions().placeholder(R.drawable.profile))
                        .into(holder.profile_pic);


                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profile_intent = new Intent(getActivity(),ProfileActivity.class);
                        profile_intent.putExtra("UID",model.getID());
                        startActivity(profile_intent);
                    }
                });

            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.notifyDataSetChanged();


    }


    @Override
    public void onStart() {
        super.onStart();
        if(mFirebaseAdapter != null)
                mFirebaseAdapter.startListening();


    }


    public static class MemberViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView profile_pic;
        TextView name,work;

        public MemberViewHolder(final View itemView, final Context context) {
            super(itemView);
            mView = itemView;

            profile_pic = itemView.findViewById(R.id.profile_image_member_row);
            name = itemView.findViewById(R.id.member_name_row);
            work = itemView.findViewById(R.id.pursuing_occupation);

        }
    }



    @Override
    public void onStop() {
        super.onStop();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.stopListening();

    }

}
