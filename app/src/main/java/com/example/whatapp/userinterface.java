package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class userinterface extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    viewpageadpter viewpageadpter;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    //   private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinterface);
        tabLayout=findViewById(R.id.tab);
        viewPager=findViewById(R.id.view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("chatting app");
        viewpageadpter=new viewpageadpter(getSupportFragmentManager());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(viewpageadpter);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //mediaPlayer=MediaPlayer.create(this,R.raw.ringing);
        //   mediaPlayer.stop();
        if(currentUser!=null) {
            currentUserID = mAuth.getCurrentUser().getUid();
            RootRef = FirebaseDatabase.getInstance().getReference();
        }
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("user");
        userRef.child(currentUserID).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();

                    userRef.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(userinterface.this, videocall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef.child(currentUserID).child("Ringing").removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef.child(currentUserID).child("calling").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("calling")) {
                    String  callingid1=snapshot.child("calling").getValue().toString();
                    userRef.child(callingid1).child("Ringing")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild("Ringing")||!snapshot.hasChild("picked")){
                                        //  String  callingid=snapshot.child("calling").getValue().toString();

                                        userRef.child(currentUserID).child("calling").removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }



                                                });
                                        userRef.child(currentUserID).child("calling").removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                    }
                                    else{

                                        Intent intent = new Intent(userinterface.this, videocall.class);
                                        intent.putExtra("uid",callingid1);
                                        // intent.putExtra("name",  messageReceiverName);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        DatabaseReference userRef1= FirebaseDatabase.getInstance().getReference().child("user1");
        userRef1.child(currentUserID).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();
                    userRef1.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(userinterface.this, datacall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef1.child(currentUserID).child("Ringing").removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        userRef1.child(currentUserID).child("calling").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("calling")) {
                    String  callingid1=snapshot.child("calling").getValue().toString();
                    userRef1.child(callingid1).child("Ringing")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild("Ringing")||!snapshot.hasChild("picked")){
                                      //  String  callingid=snapshot.child("calling").getValue().toString();

                                                            userRef1.child(currentUserID).child("calling").removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                        }



                                                });
                                        userRef1.child(currentUserID).child("calling").removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                    }
                                                });
                                    }
                                    else{

                                        Intent intent = new Intent(userinterface.this, datacall.class);
                                        intent.putExtra("uid",callingid1);
                                        // intent.putExtra("name",  messageReceiverName);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()== R.id.setting){
            Intent intent1 = new Intent(userinterface.this,update.class);
            startActivity(intent1);
        }
        if(item.getItemId()== R.id.signout){
            if( FirebaseAuth.getInstance().getCurrentUser().getUid()!=null)  FirebaseAuth.getInstance().signOut();
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onStart()
    {
        super.onStart();

        if (currentUser != null)
        {
            updateUserStatus("online");




        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (currentUser != null)
        {
            updateUserStatus("offline");
        }
    }


    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }

}