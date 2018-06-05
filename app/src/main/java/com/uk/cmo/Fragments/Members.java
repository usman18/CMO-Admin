package com.uk.cmo.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uk.cmo.Activities.Profile;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by usman on 22-02-2018.
 */

public class Members extends Fragment {
    private ArrayList<String> name_list;
    private HashMap<String,String> id_map;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private ValueEventListener listener;
    private SearchView searchView;
    private ImageView filter;
    private DatabaseReference reference;
    private Thread thread;
    private Query query;
    private String choice= Constants.REPRESENTATIVES;

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


//                Query query1=reference.orderByChild("name")
//                        .startAt(query).endAt(query+"\uf8ff");
//                setUpAdapter(query1);
//                mFirebaseAdapter.startListening();


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query=reference.child(choice).orderByChild("name")
                        .startAt(newText).endAt(newText+"\uf8ff");
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

//        search_textview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String name=search_textview.getText().toString().trim();
//                String uid=id_map.get(name);
//
//                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(uid)){
//
//                    Intent profile_intent=new Intent(getActivity(), Profile.class);
//                    profile_intent.putExtra("UID",uid);
//                    startActivity(profile_intent);
//
//                }
//
//            }
//        });

    }

    private void showAlertDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        View view=LayoutInflater.from(getContext())
                .inflate(R.layout.filter_dialog,null,false);

        builder.setView(view);
        final Spinner filter_spinner=view.findViewById(R.id.filter_spinner);
        TextView apply=view.findViewById(R.id.apply_button);
        TextView cancel=view.findViewById(R.id.cancel_button);

        final AlertDialog alertDialog=builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=filter_spinner.getSelectedItemPosition();

                if (position==0){
                    choice=Constants.REPRESENTATIVES;
                }else if (position==1) {
                    choice = Constants.ALLUSERS;
                }
                    query=reference.child(choice).orderByChild("name");
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
        filter=view.findViewById(R.id.filter);
//        name_list=new ArrayList<>();
//        id_map=new HashMap<>();
//        search_textview=view.findViewById(R.id.search);
        searchView=view.findViewById(R.id.search_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        query = reference.child(choice).orderByChild("name");

    }

//Todo                 query=reference.orderByChild("name").startAt(search).endAt(search+"\uf8ff");


    private void setUpAdapter(Query query){


        final FirebaseRecyclerOptions<Person> options
                =new FirebaseRecyclerOptions.Builder<Person>()
                .setQuery(query,Person.class)
                .build();

        mFirebaseAdapter=new FirebaseRecyclerAdapter<Person,MemberViewHolder>(options){

            @Override
            public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.member_row,parent,false);
                return new MemberViewHolder(view,getContext());
            }

            @Override
            protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull final Person model) {
//                Log.d("MEMBERS:",model.getName());
                holder.name.setText(model.getName());
                holder.relation.setText(model.getRelation());


                Glide.with(getContext())
                        .load(model.getProfile_pic())
                        .apply(new RequestOptions().placeholder(R.drawable.profile))
                        .into(holder.profile_pic);


                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     //   Toast.makeText(getContext(),model.getName(),Toast.LENGTH_SHORT).show();
                        Intent profile_intent=new Intent(getActivity(),Profile.class);
                        profile_intent.putExtra("UID",model.getID());
                        startActivity(profile_intent);
                    }
                });

            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.notifyDataSetChanged();


    }
//
//    private void setupFirebaseAdapter() {
//        Query query=reference.orderByChild("name");
//        final FirebaseRecyclerOptions<Person> options
//                =new FirebaseRecyclerOptions.Builder<Person>()
//                .setQuery(query,Person.class)
//                .build();
//        new FetchInfo(options).execute();
//    }


    @Override
    public void onStart() {
        super.onStart();
        if(mFirebaseAdapter!=null)
                mFirebaseAdapter.startListening();

//        member_ref=FirebaseDatabase.getInstance().getReference("AllUsers");
//        listener=member_ref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                       name_list.clear();
//                       id_map.clear();
//
//                       for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                           String name=snapshot.child("name").getValue(String.class);
//                           String id=snapshot.getKey();
//                           name_list.add(name);
//                           id_map.put(name,id);
//                       }
//
//                        adapter=new ArrayAdapter<String>(getActivity(),
//                        android.R.layout.simple_spinner_dropdown_item,
//                        name_list);
//                        search_textview.setAdapter(adapter);
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

    }


    public static class MemberViewHolder extends RecyclerView.ViewHolder{


        View mview;
        ImageView profile_pic;
        TextView name,relation;



        public MemberViewHolder(final View itemView, final Context context) {
            super(itemView);
            mview=itemView;

            profile_pic=itemView.findViewById(R.id.profile_image_member_row);
            name=itemView.findViewById(R.id.member_name_row);
            relation=itemView.findViewById(R.id.member_Relation_row);

        }
    }


    class Fetch extends AsyncTask<Void,Void,Void>{
        FirebaseRecyclerOptions options;
        public Fetch(FirebaseRecyclerOptions options) {
           this.options=options;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if(firebaseAuth!=null){
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFirebaseAdapter=new FirebaseRecyclerAdapter<Person,MemberViewHolder>(options){

                                @Override
                                public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                                    View view=LayoutInflater.from(parent.getContext())
                                            .inflate(R.layout.member_row,parent,false);
                                    return new MemberViewHolder(view, getContext());
                                }

                                @Override
                                protected void onBindViewHolder(@NonNull MemberViewHolder holder, int position, @NonNull Person model) {
                                    holder.name.setText(model.getName());
                                    holder.relation.setText(model.getRelation());

                                    Picasso.with(getContext())
                                            .load(model.getProfile_pic())
                                            .placeholder(R.drawable.profile)
                                            .into(holder.profile_pic);

                                }
                            };



                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mFirebaseAdapter!=null)
            mFirebaseAdapter.stopListening();

//        if(reference!=null )
//            reference.removeEventListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(thread!=null){
            thread.interrupt();
            thread=null;
        }
    }
}
