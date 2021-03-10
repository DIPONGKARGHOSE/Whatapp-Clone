package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static com.example.whatapp.R.id.helloboss;

public class videocall extends AppCompatActivity {
private TextView namecontext;
private ImageView profilcimage;
private ImageView cancelcall,makecall;
private  String callingid="",ringid="", receiverUserid="",receiverUserimage="null",receiverUsername="";
    private  String senderUserid="",senderUserimage="",senderrUsername="",checker="";
//private MediaPlayer mediaPlayer;
private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);
        receiverUserid=getIntent().getExtras().get("uid").toString();
       /// receiverUsername=getIntent().getExtras().get("name").toString();
        userRef= FirebaseDatabase.getInstance().getReference().child("user");
namecontext=findViewById(R.id.videousername);
profilcimage=findViewById(helloboss);
cancelcall=findViewById(R.id.cancel_button);
makecall=findViewById(R.id.makecall);
///mediaPlayer=MediaPlayer.create(this,R.raw.ringing);
senderUserid= FirebaseAuth.getInstance().getCurrentUser().getUid();
cancelcall.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
       /// mediaPlayer.stop();
        checker="clicked";
        Toast.makeText(getApplication(), "data"+checker, Toast.LENGTH_SHORT).show();
        ///mediaPlayer.stop();
        hello();

    }
});
        makecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              ///  mediaPlayer.stop();
                final HashMap<String,Object>callingpickupmap=new HashMap<>();

               callingpickupmap.put("picked","picked");
                userRef.child(senderUserid).child("Ringing").updateChildren(callingpickupmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                           /// mediaPlayer.stop();
                            Intent intent=new Intent(videocall.this,videochat.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });


            }
        });
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(senderUserid).hasChild("Ringing")&&!snapshot.child(senderUserid).hasChild("calling")){
                    makecall.setVisibility(View.VISIBLE);
                  ///  mediaPlayer.start();

                }
                if(snapshot.child(receiverUserid).child("Ringing").hasChild("picked")){
                  ///  mediaPlayer.stop();
                    Intent intent=new Intent(videocall.this,videochat.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

userRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.child(receiverUserid).exists()){
          if(snapshot.child(receiverUserid).child("image").exists())  receiverUserimage=snapshot.child(receiverUserid).child("image").getValue().toString();
            receiverUsername=snapshot.child(receiverUserid).child("phone").getValue().toString();
            namecontext.setText(receiverUsername);
          //  Picasso.get().load(rec).placeholder(R.drawable.profile).into(messageViewHolder.receiverProfileImage);
          if(receiverUserimage!=null)  Picasso.get().load(receiverUserimage).placeholder(R.drawable.profile).into(profilcimage);

        }
        if(snapshot.child(senderUserid).exists()){
            if(snapshot.child(senderUserid).child("image").exists()) senderUserimage=snapshot.child(senderUserid).child("image").getValue().toString();
            senderrUsername=snapshot.child(senderUserid).child("phone").getValue().toString();


        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.child(receiverUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!checker.equals("clicked")&& !snapshot.hasChild("calling")&& !snapshot.hasChild("Ringing")){
                    userRef.child(senderUserid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!checker.equals("clicked")&& !snapshot.hasChild("calling")&& !snapshot.hasChild("Ringing")){
                             ///   mediaPlayer.start();
                                final HashMap<String,Object>callingInfo=new HashMap<>();

                                callingInfo.put("calling",receiverUserid);
                                userRef.child(senderUserid).child("calling").updateChildren(callingInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            final HashMap<String,Object>ringingInfo=new HashMap<>();

                                            ringingInfo.put("ringing",senderUserid);
                                            userRef.child(receiverUserid).child("Ringing").updateChildren(ringingInfo);
                                           /// mediaPlayer.start();
                                        }
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
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(senderUserid).hasChild("Ringing")&&!snapshot.child(senderUserid).hasChild("calling")){
                    makecall.setVisibility(View.VISIBLE);

                }
                if(snapshot.child(receiverUserid).child("Ringing").hasChild("picked")){
                  ///  mediaPlayer.stop();
                    Intent intent=new Intent(videocall.this,videochat.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void  hello(){
      ///  mediaPlayer.stop();
        userRef.child(senderUserid).child("calling")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.hasChild("calling")){
                            callingid=snapshot.child("calling").getValue().toString();
                        userRef.child(callingid)
                                .child(("Ringing")).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            userRef.child(senderUserid).child("calling").removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                         ///   mediaPlayer.stop();
                                                            Intent intent = new Intent(videocall.this, Setting.class);

                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                        }
                                    }
                                });
                        }
                        else{
                          ///  mediaPlayer.stop();
                            Intent intent = new Intent(videocall.this, Setting.class);

                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        userRef.child(senderUserid).child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.hasChild("ringing")){
                           ringid=snapshot.child("ringing").getValue().toString();
                            userRef.child(ringid)
                                    .child(("calling")).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                userRef.child(senderUserid).child("Ringing").removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(videocall.this, Setting.class);

                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                        else{
                            Intent intent = new Intent(videocall.this, Setting.class);

                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    protected void onStop()
    {
        super.onStop();

      ///  mediaPlayer.stop();
    }



    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        ///mediaPlayer.stop();
    }

}