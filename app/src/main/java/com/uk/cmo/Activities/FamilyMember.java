package com.uk.cmo.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uk.cmo.Adapters.MemberAdapter;
import com.uk.cmo.Model.Person;
import com.uk.cmo.R;

import java.util.ArrayList;

import static com.uk.cmo.Activities.AccountDetailsActivity.familyMember;
public class FamilyMember extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference member_reference;
    private DatabaseReference reference;
    private ValueEventListener reference_listener;
    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private TextView message_txt;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private FloatingActionButton submit_members;
    private RecyclerView.Adapter adapter;
    private ArrayList<Person> personArrayList;
    private ProgressBar load_progress;
    private Thread updateThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member);

        setUpInstances();

    }

    @Override
    protected void onStart() {
        super.onStart();
        UpdateUIThread();
    }

    private void UpdateUIThread() {

        updateThread=new Thread(){
            @Override
            public void run() {
                super.run();
                if(firebaseAuth!=null){
                    try{
                        updateThread.sleep(250);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                member_reference=reference.child("Members")
                                        .child(firebaseAuth.getCurrentUser().getUid());
                                reference_listener=member_reference
                                        .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount()==0){
                                            load_progress.setVisibility(View.INVISIBLE);
                                            message_txt.setVisibility(View.VISIBLE);
                                        }else {
                                            personArrayList.clear();
                                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                                Person person=snapshot.getValue(Person.class);
                                                personArrayList.add(person);
                                            }
                                            load_progress.setVisibility(View.INVISIBLE);
                                            recyclerView.setVisibility(View.VISIBLE);
                                            adapter=new MemberAdapter(getApplicationContext(),personArrayList);
                                            recyclerView.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        updateThread.start();

    }

    private void setUpInstances() {

        reference=FirebaseDatabase.getInstance().getReference();
        load_progress=findViewById(R.id.load_member_progress);
        message_txt=findViewById(R.id.message_member_textview);
        submit_members=findViewById(R.id.submit_members);
        firebaseAuth=FirebaseAuth.getInstance();
        personArrayList=new ArrayList<>();
        recyclerView=findViewById(R.id.member_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        submit_members.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.add_member,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.add_member){

            familyMember=true;               //it is made false later, do not worry about that
            Intent intent=new Intent(FamilyMember.this,AccountDetailsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        builder=new AlertDialog.Builder(this);

        builder.setTitle("Confirmation");
        builder.setMessage("Have you submitted details of all the members in your family ? ");
        builder.setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.cancel();
                showDialog();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void showDialog() {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Details Submitted");
        builder.setMessage("You can update yours as well as your family member details whenever you wish to in options menu !");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                markMemberSetup();
                Intent main_screen=new Intent(FamilyMember.this,MainScreenActivity.class);
                main_screen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(main_screen);
            }
        });

        alertDialog=builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void markMemberSetup() {

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("membersetup");
        databaseReference.setValue(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(reference!=null && member_reference!=null){
            member_reference.removeEventListener(reference_listener);
        }

    }

}
