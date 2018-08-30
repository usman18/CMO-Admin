package com.uk.cmo.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;

import java.util.ArrayList;
/**
 * Created by usman on 19-02-2018.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    Context context;
    ArrayList<Person> personArrayList;

    public MemberAdapter(Context context, ArrayList<Person> personArrayList) {

        this.context = context;
        this.personArrayList = personArrayList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View member_row= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_row,parent,false);
        return new ViewHolder(member_row);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Person person=personArrayList.get(position);
        holder.name.setText(person.getName());
        holder.relation.setText(person.getRelation());

        Picasso.with(context)
                .load(person.getProfile_pic())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.profile)
                .into(holder.profile_pic);

    }

    @Override
    public int getItemCount() {
        return personArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profile_pic;
        TextView name,relation;

        public ViewHolder(View itemView) {

            super(itemView);

            profile_pic=itemView.findViewById(R.id.profile_image_member_row);
            name=itemView.findViewById(R.id.member_name_row);
            relation=itemView.findViewById(R.id.pursuing_occupation);


        }

    }
}
