package com.example.whatapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chat extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private View PrivateChatsView;
    private String mParam1;
    String s;
    private String mParam2;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootref;
    String currentuserid;

    private StorageReference userprofileimageref;
    List<profileinfo> postlist= new ArrayList<>();
    private RecyclerView recyclerView;
    private  contactadpter contactadpter;
    private Map ma;
    Map<String, String> map = new HashMap<String, String>();
    public static final int REQUEST_READ_CONTACTS = 79;
    public chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment chat.
     */
    // TODO: Rename and change types and number of parameters
    public static chat newInstance(String param1, String param2) {
        chat fragment = new chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment


        PrivateChatsView=inflater.inflate(R.layout.fragment_chat, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();
        recyclerView = PrivateChatsView.findViewById(R.id.chat_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        contactadpter = new contactadpter(getContext().getApplicationContext(), postlist, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                profileinfo pos = postlist.get(position);

                // Toast.makeText(getApplicationContext(),  pos.getUid()+ " is selected!", Toast.LENGTH_SHORT).show();
                if (!currentuserid.equals(pos.getUid())) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("uid", pos.getUid());
                    intent.putExtra("name", pos.getName());
                    intent.putExtra("image", pos.getImage());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "your click your post", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }


        });

        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("user");
        userRef.child(currentuserid).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();
                    userRef.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(getContext(), videocall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef.child(currentuserid).child("Ringing").removeValue()
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

        DatabaseReference userRef1= FirebaseDatabase.getInstance().getReference().child("user1");
        userRef1.child(currentuserid).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();

                    userRef1.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(getContext(), datacall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef1.child(currentuserid).child("Ringing").removeValue()
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

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
    ref.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            postlist.clear();
            for (final DataSnapshot idSnapshot : dataSnapshot.getChildren()) {

                profileinfo poi = idSnapshot.getValue(profileinfo.class);
             //   Toast.makeText(getContext(), "data" + poi.getPhone(), Toast.LENGTH_SHORT).show();
                if (poi != null) {
                   String sp = map.get(poi.getPhone());

              //      Toast.makeText(getContext(), sp + "map", Toast.LENGTH_LONG).show();

                    if (sp != null) {
                       poi.setName(map.get(poi.getPhone()));
                       postlist.add(poi);

                   }

                }

            }

            contactadpter = new contactadpter(getContext().getApplicationContext(), postlist, new RecyclerTouchListener.ClickListener() {

                @Override
                public void onClick(View view, int position) {

                    profileinfo pos = postlist.get(position);

                    // Toast.makeText(getApplicationContext(),  pos.getUid()+ " is selected!", Toast.LENGTH_SHORT).show();
                    if (!currentuserid.equals(pos.getUid())) {
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                       if(pos.getUid()!=null) intent.putExtra("uid", pos.getUid());
                       else intent.putExtra("uid", "null");
                       if(pos.getName()!=null) intent.putExtra("name", pos.getName());
                       else intent.putExtra("name", "null");
                       if(pos.getImage()!=null) intent.putExtra("image", pos.getImage());
                       else intent.putExtra("image", "null");
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext().getApplicationContext(), "your click your post", Toast.LENGTH_LONG).show();
                    }


                }

                @Override
                public void onLongClick(View view, int position) {

                }


            });
            recyclerView.setAdapter(contactadpter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    profileinfo poi=new profileinfo();
       poi.setUid("y50jYxaHd6emeKmmhsxRw19Vic12");
       poi.setImage("https://firebasestorage.googleapis.com/v0/b/chatting-app-caadf.appspot.com/o/profile%20image%2FiV9Y7w5DcdMlQGOiuvZdTx3dF2o2.jpg?alt=media&token=a63a27f9-0041-4c30-96c1-fb8087fcda0f");
       poi.setName("boss");
        poi.setPhone("+8801677641211");
     poi.setPhone("java");

      postlist.add(poi);

        contactadpter = new contactadpter(getContext().getApplicationContext(), postlist, new RecyclerTouchListener.ClickListener() {

            @Override
            public void onClick(View view, int position) {

                profileinfo pos = postlist.get(position);

                // Toast.makeText(getApplicationContext(),  pos.getUid()+ " is selected!", Toast.LENGTH_SHORT).show();
                if (!currentuserid.equals(pos.getUid())) {
                    Intent intent = new Intent(getContext(), ChatActivity.class);
                    intent.putExtra("uid", pos.getUid());
                    intent.putExtra("name", pos.getName());
                    intent.putExtra("image", pos.getImage());
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "your click your post", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }


        });
        recyclerView.setAdapter(contactadpter);
        contactadpter.notifyDataSetChanged();
        return PrivateChatsView;



    }
    public void onStart()
    {
        super.onStart();


        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getAllContacts();
        } else {
            requestPermission();
        }
        myfun();
    }
   private void myfun(){


       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user");
       ref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               postlist.clear();
               for (final DataSnapshot idSnapshot : dataSnapshot.getChildren()) {

                   profileinfo poi = idSnapshot.getValue(profileinfo.class);
                 //  Toast.makeText(getContext(), "data" + poi.getPhone(), Toast.LENGTH_SHORT).show();
                   if (poi != null) {
                       String sp = map.get(poi.getPhone());

                       //      Toast.makeText(getContext(), sp + "map", Toast.LENGTH_LONG).show();

                        if (sp != null) {
                            poi.setName(map.get(poi.getPhone()));
                            postlist.add(poi);
                       s="";
                        }


                   }

               }
               contactadpter = new contactadpter(getContext().getApplicationContext(), postlist, new RecyclerTouchListener.ClickListener() {

                   @Override
                   public void onClick(View view, int position) {

                       profileinfo pos = postlist.get(position);

                       // Toast.makeText(getApplicationContext(),  pos.getUid()+ " is selected!", Toast.LENGTH_SHORT).show();
                       if (!currentuserid.equals(pos.getUid())) {
                           Intent intent = new Intent(getContext(), ChatActivity.class);
                           if(pos.getUid()!=null) intent.putExtra("uid", pos.getUid());
                           else intent.putExtra("uid", "null");
                           if(pos.getName()!=null) intent.putExtra("name", pos.getName());
                           else intent.putExtra("name", "null");
                           if(pos.getImage()!=null) intent.putExtra("image", pos.getImage());
                           else intent.putExtra("image", "null");
                           startActivity(intent);
                       } else {
                           Toast.makeText(getContext().getApplicationContext(), "your click your post", Toast.LENGTH_LONG).show();
                       }


                   }

                   @Override
                   public void onLongClick(View view, int position) {

                   }


               });
               recyclerView.setAdapter(contactadpter);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

       //  profileinfo poi=new profileinfo();
       // poi.setUid("y50jYxaHd6emeKmmhsxRw19Vic12");
       ///    poi.setImage("https://firebasestorage.googleapis.com/v0/b/chatting-app-caadf.appspot.com/o/profile%20image%2FiV9Y7w5DcdMlQGOiuvZdTx3dF2o2.jpg?alt=media&token=a63a27f9-0041-4c30-96c1-fb8087fcda0f");
       //  poi.setName("boss");
       //   poi.setPhone("+8801677641211");
       //  poi.setPhone("java");

       // postlist.add(poi);

       contactadpter = new contactadpter(getContext().getApplicationContext(), postlist, new RecyclerTouchListener.ClickListener() {

           @Override
           public void onClick(View view, int position) {

               profileinfo pos = postlist.get(position);

               // Toast.makeText(getApplicationContext(),  pos.getUid()+ " is selected!", Toast.LENGTH_SHORT).show();
               if (!currentuserid.equals(pos.getUid())) {
                   Intent intent = new Intent(getContext(), ChatActivity.class);
                   intent.putExtra("uid", pos.getUid());
                   intent.putExtra("name", pos.getName());
                   intent.putExtra("image", pos.getImage());
                   startActivity(intent);
               } else {
                   Toast.makeText(getContext().getApplicationContext(), "your click your post", Toast.LENGTH_LONG).show();
               }


           }

           @Override
           public void onLongClick(View view, int position) {

           }


       });
       recyclerView.setAdapter(contactadpter);
       contactadpter.notifyDataSetChanged();

   }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    private void getAllContacts() {
       // ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                // nameList.add(name);
                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String phon="";
                        for(int i=0;i<phoneNo.length();i++){
                            if(phoneNo.charAt(i)=='1'||phoneNo.charAt(i)=='2'||phoneNo.charAt(i)=='3'||phoneNo.charAt(i)=='4'||phoneNo.charAt(i)=='5'||phoneNo.charAt(i)=='6'||phoneNo.charAt(i)=='7'||phoneNo.charAt(i)=='8'||phoneNo.charAt(i)=='9'||phoneNo.charAt(i)=='0'||phoneNo.charAt(i)=='+'){
                                phon +=phoneNo.charAt(i);
                            }
                        }
                        if(phoneNo.charAt(0)=='0')phon="+88"+phon;
                        map.put(phon,name);
                        rootref.child("members").child(currentuserid).child(phon).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }
                            }
                        });
                      ///  nameList.add(phon);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

    }
}

