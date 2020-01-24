package com.ahmed.martin.wassal_delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {

    private EditText firstName, lastName, phoneNumber, ssnNumber, Email, Password;
    private TextView Address;
    private CheckBox motorcycle, bicycle, car, subway, train;
    private Button next;

    private String userId;
    private Boolean get_User_Data;
    private user_data user, useradd;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        firstName = findViewById(R.id.fName);
        lastName = findViewById(R.id.lName);
        phoneNumber = findViewById(R.id.pNumber);
        ssnNumber = findViewById(R.id.ssnNumber);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Address = findViewById(R.id.address);
        motorcycle = findViewById(R.id.motorcycle);
        bicycle = findViewById(R.id.Bicycle);
        car = findViewById(R.id.Car);
        subway = findViewById(R.id.Subway);
        train = findViewById(R.id.train);

        next = findViewById(R.id.nextBtn);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = (user_data) getIntent().getSerializableExtra("user");
        if(user != null)


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check()) {
                    put_data_in_user_class_with_address();
                    Log.d("kkkk", useradd.getEmail()+ useradd.getAddress() + useradd.getAddress_lat());
                    if (!motorcycle.isChecked()) {
                        if (!car.isChecked()) {
                            mAuth.createUserWithEmailAndPassword(Email.getText().toString().trim(), Password.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                userId = mAuth.getCurrentUser().getUid();
                                                mDatabase.child("person").child(userId).setValue(user);
                                                DatabaseReference ref = mDatabase.child("delivery").child(userId).child("delivery type");
                                                if (bicycle.isChecked()) {
                                                    ref.child("bicycle").setValue("bicycle");
                                                }
                                                if (subway.isChecked()) {
                                                    ref.child("subway").setValue("subway");
                                                }
                                                if (train.isChecked()) {
                                                    ref.child("train").setValue("train");
                                                }
                                                Intent main = new Intent(sign_up.this, MainActivity.class);
                                                main.putExtra("user", useradd);
                                                startActivity(main);
                                                finish();
                                            }
                                        }
                                });
                        } else {
                            Intent main = new Intent(sign_up.this, LicenseActivity.class);
                            main.putExtra("password", Password.getText().toString().trim());
                            main.putExtra("user", useradd);
                            if (bicycle.isChecked()) {
                                main.putExtra("bicycle", true);
                            }
                            if (subway.isChecked()) {
                                main.putExtra("subway", true);
                            }
                            if (train.isChecked()) {
                                main.putExtra("train", true);
                            }
                            main.putExtra("car", true);
                            startActivity(main);
                            finish();
                        }
                    } else {
                        if (!car.isChecked()) {
                            Intent main = new Intent(sign_up.this, LicenseActivity.class);
                            main.putExtra("user", useradd);
                            if (bicycle.isChecked()) {
                                main.putExtra("bicycle", true);
                            }
                            if (subway.isChecked()) {
                                main.putExtra("subway", true);
                            }
                            if (train.isChecked()) {
                                main.putExtra("train", true);
                            }
                            main.putExtra("motor", true);
                            main.putExtra("password", Password.getText().toString().trim());
                            startActivity(main);
                            finish();
                        } else {
                            Intent main = new Intent(sign_up.this, LicenseActivity.class);
                            main.putExtra("user", useradd);
                            main.putExtra("car", true);
                                if (bicycle.isChecked()) {
                                    main.putExtra("bicycle", true);
                                }
                                if (subway.isChecked()) {
                                    main.putExtra("subway", true);
                                }
                                if (train.isChecked()) {
                                    main.putExtra("train", true);
                                }
                            main.putExtra("motor", true);
                            main.putExtra("password", Password.getText().toString().trim());
                            startActivity(main);
                            finish();
                        }
                    }
                }
            }
        });
        get_data_from_class_user();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void put_data_in_user_class(){
        user = new user_data();
        user.setFirst_name(firstName.getText().toString().trim());
        user.setLast_name(lastName.getText().toString().trim());
        user.setPhoneNumber(phoneNumber.getText().toString().trim());
        user.setSsnNumber(ssnNumber.getText().toString().trim());
        user.setEmail(Email.getText().toString().trim());
    }
    public void put_data_in_user_class_with_address(){
        useradd = new user_data();
        useradd.setFirst_name(firstName.getText().toString().trim());
        useradd.setLast_name(lastName.getText().toString().trim());
        useradd.setPhoneNumber(phoneNumber.getText().toString().trim());
        useradd.setSsnNumber(ssnNumber.getText().toString().trim());
        useradd.setEmail(Email.getText().toString().trim());
        useradd.setAddress(user.getAddress());
        useradd.setAddress_lat(user.getAddress_lat());
        useradd.setAddress_long(user.getAddress_long());

    }

    public void get_user_address(View view){
        put_data_in_user_class();
        Intent map = new Intent(sign_up.this, MapsActivity.class);
        map.putExtra("user", user);
        map.putExtra("signup", "yes");
        map.putExtra("edit" , "no");
        startActivity(map);
    }

    public void get_data_from_class_user(){
        if(user != null) {
            firstName.setText(user.getFirst_name());
            lastName.setText(user.getLast_name());
            phoneNumber.setText(user.getPhoneNumber());
            ssnNumber.setText(user.getSsnNumber());
            Address.setText(user.getAddress());
            Email.setText(user.getEmail());
        }
    }

    private Boolean check(){
        if(TextUtils.isEmpty(firstName.getText().toString())){
            firstName.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(lastName.getText().toString())){
            lastName.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(phoneNumber.getText().toString())){
            phoneNumber.setError("can't be empty");
            return false;
        }
        if(phoneNumber.getText().toString().length() != 11){
            phoneNumber.setError("Enter right number");
            return false;
        }
        if(TextUtils.isEmpty(ssnNumber.getText().toString())){
            ssnNumber.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(Email.getText().toString())){
            Email.setError("can't be empty");
            return false;
        }
        if(TextUtils.isEmpty(Password.getText().toString())){
            Password.setError("can't be empty");
            return false;
        }
        return true;
    }
}
