package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.whatapp.chat.REQUEST_READ_CONTACTS;

public class Setting extends AppCompatActivity {
    CircleImageView circleImageView;
    EditText editText,editText1;
    String currentuserid;
    private FirebaseAuth firebaseAuth;
   private DatabaseReference rootref;
    Map<String, String> map = new HashMap<String, String>();
    private StorageReference userprofileimageref;
    Button button;
    private ProgressDialog loadingBar;
    int f;
    private static final int Gallerypicker=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        circleImageView=findViewById(R.id.profile_image);
        editText=findViewById(R.id.name);
        editText1=findViewById(R.id.status);
        button=findViewById(R.id.update);
  f=0;
        loadingBar = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();
        userprofileimageref= FirebaseStorage.getInstance().getReference().child("profile image");
       // Userprofileimageref
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallerypicker);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Set Profile Update");
                loadingBar.setMessage("Please wait, your profile  is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                String name=editText.getText().toString();
                String status=editText1.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please Enter your Name", Toast.LENGTH_LONG).show();
                }
                else {
                    rootref.child("user").child(currentuserid).child("name").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                    rootref.child("user").child(currentuserid).child("uid").setValue(currentuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                    if(!status.isEmpty()){
                        rootref.child("user").child(currentuserid).child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }
                            }
                        });
                    }

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(currentuserid);
                    databaseReference.addValueEventListener(new ValueEventListener() {


                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String s = (String) dataSnapshot.child("image").getValue();

                           if(s!=null) Picasso.get().load(s).into(circleImageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }
        });
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("user").child(currentuserid);

        databaseReference1.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s = (String) dataSnapshot.child("image").getValue();
                String s1 = (String) dataSnapshot.child("name").getValue();
                String s2 = (String) dataSnapshot.child("status").getValue();
                if(s!=null) Picasso.get().load(s).into(circleImageView);
                if (s1!=null)editText.setText(s1);
                if(s2!=null)editText1.setText(s2);
                if(s1!=null){
                    loadingBar.dismiss();
                    Intent intent = new Intent(Setting.this, userinterface.class);

                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });
        //rootref.child("user").child(currentuserid).child("image")



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallerypicker&&resultCode==RESULT_OK&&data!=null){
            Uri ImageUri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Toast.makeText(getApplicationContext(), "helloio.", Toast.LENGTH_LONG).show();
            if(resultCode==RESULT_OK){
                loadingBar.setTitle("Set Profile Image");
                loadingBar.setMessage("Please wait, your profile image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                Toast.makeText(getApplicationContext(),
                        "hi", Toast.LENGTH_LONG).show();
                Uri resultUri=result.getUri();
                StorageReference filepath=userprofileimageref.child(currentuserid+".jpg");
                filepath.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        final String downloadUri= uri.toString();
                                        // complete the rest of your code
                                        rootref.child("user").child(currentuserid).child("image").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    loadingBar.dismiss();

                                                }
                                            }
                                        });
                                    }
                                });

                            }
                        });

            }

        }
    }
    public void onStart()
    {
        super.onStart();

     ///   if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
    ///        getAllContacts();
    ///    } else {
       //     requestPermission();
       /// }
    }
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        ContentResolver cr = this.getContentResolver();
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


                      ///  rootref.child("members").child(currentuserid).child(phon).setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                     ///       @Override
                         ///   public void onComplete(@NonNull Task<Void> task) {
                           ///     if(task.isSuccessful()){

                            ///    }
                         ///   }
                      //  });
                        ///  nameList.add(phon);
                    }
                    pCur.close();
                }
            }
            f=1;
        }
        if (cur != null) {
            cur.close();
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}