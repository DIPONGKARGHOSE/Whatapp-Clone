package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
 private EditText MessageInputText;
 private ImageButton sendbutton,sendfilebutton,videocall,phonecall;
    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSenderID;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private CircleImageView userImage;
    private TextView userName, userLastSeen;
 private ProgressDialog  loadingBar;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
private String checker="";
    private String saveCurrentTime, saveCurrentDate;
 private Uri fileuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar =  findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setDisplayShowCustomEnabled(true);
        loadingBar=new ProgressDialog(this);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        MessageInputText=findViewById(R.id.input_message);
        sendbutton=findViewById(R.id.send_message_btn);
        sendfilebutton=findViewById(R.id.send_files_btn);
        messageReceiverID = getIntent().getExtras().get("uid").toString();
        messageReceiverName = getIntent().getExtras().get("name").toString();
        messageReceiverImage = getIntent().getExtras().get("image").toString();
        userName = (TextView) findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        videocall=findViewById(R.id.video);
        phonecall=findViewById(R.id.phone);
        messageAdapter = new MessageAdapter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        DisplayLastSeen();
        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile).into(userImage);
        videocall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, videocall.class);
                intent.putExtra("uid", messageReceiverID);
              ///  intent.putExtra("name",  messageReceiverName);
                startActivity(intent);
                finish();
            }
        });
        phonecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, datacall.class);
                intent.putExtra("uid", messageReceiverID);
                ///  intent.putExtra("name",  messageReceiverName);
                startActivity(intent);
                finish();
            }
        });

        sendfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence options[]=new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "Ms Word Files"
                        };
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("Select the file");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                             checker="image";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }
                        if(i==1){
                            checker="pdf";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select Pdf file"),438);
                        }
                        if(i==2){
                            checker="docx";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select MS Word file"),438);
                        }
                    }
                });
                builder.show();

            }
        });
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayLastSeen();
                String messageText=MessageInputText.getText().toString();
                if(!messageText.isEmpty()){
                    String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                    String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                    DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                            .child(messageSenderID).child(messageReceiverID).push();

                    String messagePushID = userMessageKeyRef.getKey();

                    Map messageTextBody = new HashMap();
                    messageTextBody.put("message", messageText);
                    messageTextBody.put("type", "text");
                    messageTextBody.put("from", messageSenderID);
                    messageTextBody.put("to", messageReceiverID);
                    messageTextBody.put("messageID", messagePushID);
                    messageTextBody.put("time", saveCurrentTime);
                    messageTextBody.put("date", saveCurrentDate);

                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                    messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                            MessageInputText.setText("");
                        }
                    });




                }
                else{

                    Toast.makeText(ChatActivity.this, "write something", Toast.LENGTH_LONG).show();
                }

            }
        });




    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop()
    {
        super.onStop();

        if (messageSenderID!= null)
        {
            updateUserStatus("offline");
        }
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (messageSenderID != null)
        {
            updateUserStatus("offline");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==438&&resultCode==RESULT_OK&&data!=null){
            loadingBar.setTitle("Send File");
            loadingBar.setMessage("Please wait, we are send file..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileuri=data.getData();
            if(!checker.equals("image")){
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document file");
                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                String messagePushID = userMessageKeyRef.getKey();



                StorageReference filepath=storageReference.child(messagePushID+"."+checker);
                filepath.putFile(fileuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUri= uri.toString();
                                        // complete the rest of your code
                                        Map messageTextBody = new HashMap();
                                        messageTextBody.put("message", downloadUri);
                                        messageTextBody.put("name", fileuri.getLastPathSegment());
                                        messageTextBody.put("type", checker);
                                        messageTextBody.put("from", messageSenderID);
                                        messageTextBody.put("to", messageReceiverID);
                                        messageTextBody.put("messageID", messagePushID);
                                        messageTextBody.put("time", saveCurrentTime);
                                        messageTextBody.put("date", saveCurrentDate);

                                        Map messageBodyDetails = new HashMap();
                                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                                MessageInputText.setText("");
                                            }
                                        });

                                    }
                                });

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double p=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        loadingBar.setMessage((int)p+"  % Uploading...");
                    }
                });

            }
            else if(checker.equals("image")){
        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("image file");
                String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef = "Messages/" + messageReceiverID + "/" + messageSenderID;

                DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();

                String messagePushID = userMessageKeyRef.getKey();



                StorageReference filepath=storageReference.child(messagePushID+".jpg");
               filepath.putFile(fileuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUri= uri.toString();
                                        // complete the rest of your code
                                        Map messageTextBody = new HashMap();
                                        messageTextBody.put("message", downloadUri);
                                        messageTextBody.put("name", fileuri.getLastPathSegment());
                                        messageTextBody.put("type", checker);
                                        messageTextBody.put("from", messageSenderID);
                                        messageTextBody.put("to", messageReceiverID);
                                        messageTextBody.put("messageID", messagePushID);
                                        messageTextBody.put("time", saveCurrentTime);
                                        messageTextBody.put("date", saveCurrentDate);

                                        Map messageBodyDetails = new HashMap();
                                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                        messageBodyDetails.put( messageReceiverRef + "/" + messagePushID, messageTextBody);

                                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                }
                                                MessageInputText.setText("");
                                            }
                                        });

                                    }
                                });

                            }
                        });

            }


        }



    }
    @Override
    protected void onStart()
    {
        super.onStart();
        DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("user");
        userRef.child(messageSenderID).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();
                    userRef.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(ChatActivity.this, videocall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef.child(messageSenderID).child("Ringing").removeValue()
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
        userRef1.child(messageSenderID).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")) {
                    String valuer=snapshot.child("ringing").getValue().toString();

                    userRef1.child(snapshot.child("ringing").getValue().toString()).child("calling").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild("calling")) {
                                Intent intent = new Intent(ChatActivity.this, datacall.class);
                                intent.putExtra("uid",valuer);
                                // intent.putExtra("name",  messageReceiverName);
                                startActivity(intent);
                            }
                            else{
                                userRef1.child(messageSenderID).child("Ringing").removeValue()
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

        if (messageSenderID != null)
        {
            updateUserStatus("online");
        }

        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdapter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("userState").hasChild("state"))
                        {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            String date = dataSnapshot.child("userState").child("date").getValue().toString();
                            String time = dataSnapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                userLastSeen.setText("online");
                            }
                            else if (state.equals("offline"))
                            {
                                userLastSeen.setText("Last Seen:" + date + " " + time);
                            }
                        }
                        else
                        {
                            userLastSeen.setText("offline");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

        RootRef.child("Users").child(messageSenderID).child("userState")
                .updateChildren(onlineStateMap);

    }



}