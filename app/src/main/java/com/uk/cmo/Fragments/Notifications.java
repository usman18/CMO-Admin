package com.uk.cmo.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.uk.cmo.Model.Person;
import com.uk.cmo.Model.StudyingPerson;
import com.uk.cmo.Model.WorkingPerson;
import com.uk.cmo.R;
import com.uk.cmo.Utility.Constants;
import com.uk.cmo.Utility.Date;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by usman on 15-08-2018.
 */

public class Notifications extends Fragment {
	
	private DatabaseReference reference;
	private FirebaseRecyclerAdapter mFirebaseRecyclerAdapter;
	
	private RecyclerView rv_notifications;
	private LinearLayout linearLayout;
	
	public static Notifications getInstance() {
		return new Notifications();
	}
	
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.notifications, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		rv_notifications = view.findViewById(R.id.rv_notifications);
		linearLayout = view.findViewById(R.id.linear_layout);
		rv_notifications.setLayoutManager(new LinearLayoutManager(getContext()));
		
		reference = FirebaseDatabase.getInstance().getReference(Constants.REPRESENTATIVES);
		
		
		Query query = reference.orderByChild(Constants.LEGIT)
			.equalTo(false);
		
		setupAdapter(query);
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (mFirebaseRecyclerAdapter != null)
			mFirebaseRecyclerAdapter.startListening();
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (mFirebaseRecyclerAdapter != null)
			mFirebaseRecyclerAdapter.stopListening();
		
	}
	
	private void setupAdapter(Query query) {
		
		FirebaseRecyclerOptions<Person>
			mFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Person>()
			.setQuery(query, Person.class)
			.build();
		
		mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Person, NotificationViewHolder>(mFirebaseRecyclerOptions) {
			
			@NonNull
			@Override
			public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				return new NotificationViewHolder(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.approval_layout, parent, false));
			}
			
			@Override
			protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull final Person model) {
				
				holder.tv_name.setText(model.getName());
				
				Glide.with(getContext())
					.load(model.getProfile_pic())
					.apply(new RequestOptions().placeholder(R.drawable.profile))
					.into(holder.profile_pic);
				
				if (model.getStudyingPerson() != null) {
					
					StudyingPerson studyingPerson = model.getStudyingPerson();
					holder.tv_occupation_pursuing.setText(studyingPerson.getPursuing());
					
				} else if (model.getWorkingPerson() != null) {
					
					WorkingPerson workingPerson = model.getWorkingPerson();
					holder.tv_occupation_pursuing.setText(workingPerson.getOccupation());
					
				}
				
				if (model.getProfile_created_at() != 0) {
					
					holder.tv_timestamp.setText(String.valueOf(Date.ToString(model.getProfile_created_at())));
				} else {
					
					holder.tv_timestamp.setText("--");
					
				}
				
				
				holder.btn_approve.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						//Todo : Even make legit true in User
						
						DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
						
						Map<String, Object> children = new HashMap<>();
						children.put(Constants.USERS + "/" + model.getID() + "/" + Constants.LEGIT, true);
						children.put(Constants.REPRESENTATIVES + "/" + model.getID() + "/" + Constants.LEGIT, true);
						
						mReference.updateChildren(children)
							.addOnSuccessListener(new OnSuccessListener<Void>() {
								@Override
								public void onSuccess(Void aVoid) {
									
									Snackbar.make(linearLayout, "Profile Approved !", 1000).show();
									
								}
							})
						.addOnFailureListener(new OnFailureListener() {
							@Override
							public void onFailure(@NonNull Exception e) {
								Toast.makeText(getContext(), "Error!", Toast.LENGTH_LONG)
									.show();
							}
						});
						
					}
				});
				
				holder.btn_decline.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						FirebaseDatabase.getInstance()
							.getReference(Constants.REPRESENTATIVES)
							.child(model.getID())
							.child("legit")
							.setValue(false);
						
						//Todo : Add declined by id, name and time
						
					}
				});
				
				
			}
			
			
		};
		
		rv_notifications.setAdapter(mFirebaseRecyclerAdapter);
		mFirebaseRecyclerAdapter.notifyDataSetChanged();
		
		
	}
	
	;
	
	public static class NotificationViewHolder extends RecyclerView.ViewHolder {
		
		CircleImageView profile_pic;
		TextView tv_name;
		TextView tv_occupation_pursuing;
		TextView tv_timestamp;
		
		Button btn_approve;
		Button btn_decline;
		
		public NotificationViewHolder(View itemView) {
			super(itemView);
			
			profile_pic = itemView.findViewById(R.id.profile_pic);
			tv_name = itemView.findViewById(R.id.tv_name);
			tv_occupation_pursuing = itemView.findViewById(R.id.tv_occupation_or_pursuing);
			tv_timestamp = itemView.findViewById(R.id.tv_timestamp);
			btn_approve = itemView.findViewById(R.id.btn_approve);
			btn_decline = itemView.findViewById(R.id.btn_decline);
			
		}
	}
	
}
