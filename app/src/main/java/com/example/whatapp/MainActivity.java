package com.example.whatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    String  selected_country_code,number;
    EditText editText;
    Button button;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ccp = findViewById(R.id.ccp);
        editText=findViewById(R.id.phone);
        button=findViewById(R.id.login_button);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
      if(currentUser!=null) {
          currentUserID = mAuth.getCurrentUser().getUid();
          RootRef = FirebaseDatabase.getInstance().getReference();
      }
     //   Map<Integer, String> map = new HashMap<Integer, String>();

        // Mapping string values to int keys
       // map.put(10, "Geeks");
      //  map.put(15, "4");
    //    map.put(20, "Geeks");
     //   map.put(25, "Welcomes");
        //map.put(30, "You");

        // Displaying the Map
      //  System.out.println("Initial Mappings are: " + map);

        // Getting the value of 25
      ///  System.out.println("The Value is: " + map.get(5));

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected() {
                    //Alert.showMessage(RegistrationActivity.this, ccp.getSelectedCountryCodeWithPlus());
                    selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                    //Toast.makeText(MainActivity.this, selected_country_code, Toast.LENGTH_LONG).show();
                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_country_code = ccp.getSelectedCountryCodeWithPlus();
                    number=editText.getText().toString();

                    Intent intent = new Intent(MainActivity.this, otp_verfic.class);
                    String phone=selected_country_code +number;
                 //   Toast.makeText(MainActivity.this, selected_country_code+number, Toast.LENGTH_LONG).show();
                   intent.putExtra("phone", phone);
                    startActivity(intent);
                    finish();

                  // Toast.makeText(MainActivity.this, selected_country_code+number, Toast.LENGTH_LONG).show();

                }
            });


    }
    @Override
    protected void onStart()
    {
        super.onStart();

        if (currentUser != null)
        {
            updateUserStatus("online");
            Intent intent = new Intent(MainActivity.this, Setting.class);

            startActivity(intent);
            this.finish();

        }
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
