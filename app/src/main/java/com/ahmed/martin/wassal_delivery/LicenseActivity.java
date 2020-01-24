package com.ahmed.martin.wassal_delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class LicenseActivity extends AppCompatActivity {
    private user_data user;
    private boolean selectcar, selectmotor, selectbicycle, selectsubway, selecttrain;
    private Uri urcar, urmotor;
    private ImageView carImg, motorImg;
    private Button signup;
    private String pass, userId;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);
        user =(user_data) getIntent().getSerializableExtra("user");
        selectcar = getIntent().getBooleanExtra("car", false);
        selectmotor = getIntent().getBooleanExtra("motor", false);
        selectbicycle = getIntent().getBooleanExtra("bicycle", false);
        selectsubway = getIntent().getBooleanExtra("subway", false);
        selecttrain = getIntent().getBooleanExtra("train", false);
        Log.d("jhhj", selectcar + " " + selectmotor);
        pass = getIntent().getStringExtra("password");

        carImg = findViewById(R.id.carlicense);
        motorImg = findViewById(R.id.motorcyclelicense);
        signup = findViewById(R.id.signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        if(selectmotor)
            carImg.setVisibility(View.INVISIBLE);
        if(selectcar)
            motorImg.setVisibility(View.INVISIBLE);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("kkkk", user+"  "+user.getEmail()+ " " + pass);
                mAuth.createUserWithEmailAndPassword(user.getEmail(), pass)
                        .addOnCompleteListener(LicenseActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            userId = mAuth.getCurrentUser().getUid();
                            mDatabase.child("person").child(userId).setValue(user);
                            if(selectmotor){
                                if(urmotor==null){
                                    motorImg.setBackgroundColor(Color.RED);
                                    Toast.makeText(LicenseActivity.this, "select image", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mDatabase.child("delivery").child(userId).child("delivery type").child("motorcycle").setValue("motorcycle");
                                    if(selectbicycle)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("bicycle").setValue("bicycle");
                                    if(selectsubway)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("subway").setValue("subway");
                                    if(selecttrain)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("train").setValue("train");
                                    UploadTask upmotor = mStorage.child("motorcycle").child(userId).putFile(Uri.parse(urmotor.toString()));
                                    upmotor.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LicenseActivity.this, "photo uploaded", Toast.LENGTH_LONG).show();
                                                Intent main = new Intent(LicenseActivity.this, MainActivity.class);
                                                startActivity(main);
                                                finish();
                                            }
                                        }
                                    });
                                }

                            }
                            if(selectcar){
                                if(urcar==null){
                                    carImg.setBackgroundColor(Color.RED);
                                    Toast.makeText(LicenseActivity.this, "select image", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    mDatabase.child("delivery").child(userId).child("delivery type").child("car").setValue("Car");
                                    if(selectbicycle)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("bicycle").setValue("bicycle");
                                    if(selectsubway)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("subway").setValue("subway");
                                    if(selecttrain)
                                        mDatabase.child("delivery").child(userId).child("delivery type").child("train").setValue("train");
                                    UploadTask upcar = mStorage.child("car").child(userId).putFile(Uri.parse(urcar.toString()));
                                    upcar.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LicenseActivity.this, "photo uploaded", Toast.LENGTH_LONG).show();
                                                Intent main = new Intent(LicenseActivity.this, MainActivity.class);
                                                startActivity(main);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

    }

    public void upload_image_car(View view){
        String [] permission ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permission[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[1])== PackageManager.PERMISSION_GRANTED){

            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }else
            ActivityCompat.requestPermissions(LicenseActivity.this,permission,12);

    }
    public void upload_image_motorcycle(View view){
        String [] permission ={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permission[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), permission[1])== PackageManager.PERMISSION_GRANTED){

            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,3);
        }else
            ActivityCompat.requestPermissions(LicenseActivity.this,permission,13);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // request get photos from galary
        if(requestCode==2&&resultCode==RESULT_OK) {
            urcar = data.getData();
            Picasso.with(LicenseActivity.this).load(urcar).into(carImg);
        }
        // request get photos from galary
        if(requestCode==3&&resultCode==RESULT_OK) {
            urmotor = data.getData();
            Picasso.with(LicenseActivity.this).load(urmotor).into(motorImg);
        }
        // request get photo permission
        if (requestCode==12&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,2);
        }
        // request get photo permission
        if (requestCode==13&&resultCode== RESULT_OK){
            Intent inte=new Intent(Intent.ACTION_PICK);
            inte.setType("image/*");
            startActivityForResult(inte,3);
        }
    }

    private Boolean check(){
        if(urcar==null){
            carImg.setBackgroundColor(Color.RED);
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(urmotor==null){
            motorImg.setBackgroundColor(Color.RED);
            Toast.makeText(this, "select image", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
