package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.TimeUnit;

public class otp_verfic extends AppCompatActivity {
   String mobile;
    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private ProgressDialog loadingBar;
    //The edittext to input the code
    private EditText editText1,editText2,editText3,editText4,editText5,editText6;

    //firebase auth object
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verfic);
        firebaseAuth = FirebaseAuth.getInstance();
        //initializing objects
        mAuth = FirebaseAuth.getInstance();
        editText1= findViewById(R.id.code1);
        editText2 = findViewById(R.id.code2);
        editText3 = findViewById(R.id.code3);
        editText4 = findViewById(R.id.code4);
        editText5 = findViewById(R.id.code5);
        editText6 = findViewById(R.id.code6);



        //getting mobile number from the previous activity
        //and sending the verification code to the number
        Intent intent = getIntent();
     String  mobile1 = intent.getStringExtra("phone");
       // Toast.makeText(otp_verfic.this, mobile1, Toast.LENGTH_LONG).show();
        mobile=mobile1;
        sendVerificationCode(mobile1);


        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        findViewById(R.id.otp_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code1 = editText1.getText().toString().trim();
                String code2 = editText2.getText().toString().trim();
                String code3 = editText3.getText().toString().trim();
                String code4 = editText4.getText().toString().trim();
                String code5 = editText5.getText().toString().trim();
                String code6 = editText6.getText().toString().trim();

                if (code1.isEmpty() || code2.isEmpty()|| code3.isEmpty()|| code4.isEmpty()|| code5.isEmpty()|| code6.isEmpty()) {

                    Toast.makeText(otp_verfic.this, "Enter valid code", Toast.LENGTH_LONG).show();

                    return;
                }
                String code=code1+code2+code3+code4+code5+code6;

                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
       // PhoneAuthProvider.getInstance().verifyPhoneNumber(
          //       mobile,
          //      60,
          //      TimeUnit.SECONDS,
           //    otp_verfic.this,
             //   mCallbacks);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }



    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editText1.setText(code.charAt(0));
                editText2.setText(code.charAt(1));
                editText3.setText(code.charAt(2));
                editText4.setText(code.charAt(3));
                editText5.setText(code.charAt(4));
                editText6.setText(code.charAt(5));

                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(otp_verfic.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(otp_verfic.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                            FirebaseDatabase.getInstance().getReference().child("user").child(userid).child("phone").setValue(mobile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                          //  Toast.makeText(otp_verfic.this, "please Verification Email and login", Toast.LENGTH_LONG).show();
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(otp_verfic.this, Setting.class);

                            startActivity(intent);
                            finish();



                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
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
}