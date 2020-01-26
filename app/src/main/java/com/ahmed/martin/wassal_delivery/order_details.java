package com.ahmed.martin.wassal_delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class order_details extends AppCompatActivity {

    private TextView r_name,r_phone,r_address,description,weight,provide_pay,delivery_estimate,date,s_name,s_phone,s_address;
    private ImageView order_photo;

    private order_data  order_description ;

    private user_data user_description;

    private String delivery_type , city, userId;

    private boolean accept = false;

    private Button btn_accept;
    private FirebaseAuth mAuth;
    String d_id = "d_id"; // geb el data de ya 3mr /* ...................*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
        d_id = userId;
        r_name = findViewById(R.id.r_name);
        r_phone = findViewById(R.id.r_phone);
        r_address = findViewById(R.id.r_address);
        s_name = findViewById(R.id.s_name);
        s_phone = findViewById(R.id.s_phone);
        s_address = findViewById(R.id.s_address);
        description = findViewById(R.id.description);
        weight = findViewById(R.id.weight);
        provide_pay = findViewById(R.id.provide_pay);
        delivery_estimate = findViewById(R.id.delivery_estimate);
        date = findViewById(R.id.date);
        order_photo = findViewById(R.id.order_photo);
        btn_accept = findViewById(R.id.button);

        order_description = (order_data) getIntent().getSerializableExtra("order");
        delivery_type = order_description.getDelivery_type();
        city = order_description.getCity();

        // get data from class order to show it
        get_data_from_class_order();

       // download photo
        download_order_photo();

        // get user_data
        get_user_details();

        boolean have_order = getIntent().getBooleanExtra("have_order",false);
        if(have_order)
            btn_accept.setText("Delivered Successfully");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void get_user_details() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("person").child(order_description.getS_uid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                user_description = dataSnapshot.getValue(user_data.class);
                s_address.setText(user_description.getAddress());
                s_name.setText(user_description.getFirst_name());
                s_phone.setText(user_description.getPhoneNumber());
            }

            @Override
            public void onCancelled( DatabaseError databaseError) {

            }
        });
    }

    private void download_order_photo() {

        String currentDate = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault()).format(new Date());
        final StorageReference food_img_ref = FirebaseStorage.getInstance().getReference()
                .child("order").child(currentDate).child(city).child(delivery_type).child(order_description.getOrder_id());
        food_img_ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                    Picasso.with(order_details.this).load(task.getResult()).into(order_photo);
                else
                    Toast.makeText(order_details.this,"note :"+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void get_data_from_class_order(){
        if(order_description != null) {
            date.setText(order_description.getDate());
            delivery_estimate.setText(order_description.getDelivery_estimate());
            description.setText(order_description.getDescription());
            provide_pay.setText(order_description.getProvide_pay());
            weight.setText(order_description.getWeight());
            r_address.setText(order_description.getR_address());
            r_name.setText(order_description.getR_name());
            r_phone.setText(order_description.getR_phone());
        }

    }

    public void accept_order(View view) {
        String currentDate = new SimpleDateFormat("dd-M-yyyy", Locale.getDefault()).format(new Date());
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("delivery").child(d_id).child("order");
        final DatabaseReference pending = FirebaseDatabase.getInstance().getReference().child("person")
                .child(order_description.getS_uid()).child("pending").child(currentDate).child(city)
                .child(delivery_type).child(order_description.getOrder_id());
        final StorageReference img_ref = FirebaseStorage.getInstance().getReference()
                .child("order").child(currentDate).child(city).child(delivery_type).child(order_description.getOrder_id());
        // لو هو داس ع الزرار لتانى مره  يبقى كده الاوردر وصل خلاص
        if(btn_accept.getText().equals("Delivered Successfully")){
            AlertDialog.Builder alart = new AlertDialog.Builder(this);
            alart.setTitle("do you send the order successfully...?! ");
            alart.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // remove order from database and finish activity
                    img_ref.delete();
                    ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( Task<Void> task) {
                            if(task.isSuccessful()){
                                pending.removeValue();
                                Intent main = new Intent(order_details.this,MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(main);
                                finish();
                            }else
                                Toast.makeText(order_details.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
            alart.show();

        // لو هو داس ع الزرار لاول مره يبقى هو كده وافق انه يسلم الاوردر
        }else {
            ref.setValue(order_description);
            pending.setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
            DatabaseReference remove_ref = FirebaseDatabase.getInstance().getReference().child("order").child(currentDate).
                    child(city).child(delivery_type).child(order_description.getOrder_id());
            DatabaseReference remove_saved = FirebaseDatabase.getInstance().getReference().child("person").child(order_description.getS_uid())
                    .child("saved").child(currentDate).child(city).child(delivery_type).child(order_description.getOrder_id());
            remove_ref.removeValue();
            remove_saved.removeValue();
            btn_accept.setText("Delivered Successfully");
        }

    }

    @Override
    public void onBackPressed() {
        if(btn_accept.getText().equals("Delivered Successfully"))
            Toast.makeText(this, "Deliver Order First", Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }

    public void cancel(View view) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("order").child(order_description.getDate()).
                child(city).child(delivery_type).child(order_description.getOrder_id());
        DatabaseReference saved = FirebaseDatabase.getInstance().getReference().child("person").child(order_description.getS_uid())
                .child("saved").child(order_description.getDate()).child(city).child(delivery_type).child(order_description.getOrder_id());
        order_description.setOrder_id(null);
        order_description.setDate(null);
        order_description.setDelivery_type(null);
        order_description.setCity(null);

        ref.setValue(order_description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("delivery").child(d_id).child("order");
                    ref.removeValue();
                }else
                    Toast.makeText(order_details.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        saved.setValue(order_description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference pending = FirebaseDatabase.getInstance().getReference().child("person")
                            .child(order_description.getS_uid()).child("pending");
                    pending.removeValue();
                    finish();
                }
                else
                    Toast.makeText(order_details.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
     }



    // every time press on map location of delivery updated by select it's location /*   */
    public void draw_map(View view) {
        getLastLocation();

    }

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                1
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }

    FusedLocationProviderClient mFusedLocationClient;

    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete( Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {

                                    Double delivery_lat = location.getLatitude();
                                    Double delivery_long = location.getLongitude();
                                    run_map_activity(delivery_lat,delivery_long);

                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Double delivery_lat = mLastLocation.getLatitude();
            Double delivery_long = mLastLocation.getLongitude();

            run_map_activity(delivery_lat,delivery_long);
        }
    };

    private void run_map_activity(Double d_lat , Double d_long){
        Intent map = new Intent(order_details.this,MapsActivity.class);

        map.putExtra("s_long",user_description.getAddress_long());
        map.putExtra("s_lat",user_description.getAddress_lat());

        map.putExtra("r_lat",order_description.getR_lat());
        map.putExtra("r_long",order_description.getR_long());

        // send delivery (lat,long) to activity map
        map.putExtra("d_lat",d_lat);
        map.putExtra("d_long",d_long);
        startActivity(map);
    }
  /*      */

}
