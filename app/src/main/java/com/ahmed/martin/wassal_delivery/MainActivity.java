package com.ahmed.martin.wassal_delivery;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;



import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    FusedLocationProviderClient mFusedLocationClient;


    ArrayList <ArrayList<LatLng>> points = new ArrayList<>();

    String city="";
    String delivery_type = "Car";  // mtlop mnk ya 3mr tgeb el kema de b el code

    Double delivery_lat,delivery_long;

    ListView km_list;



    ArrayAdapter <Double> adapter ;
    ArrayList <Double> km = new ArrayList<>();
    ArrayList <order_data> orders_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        km_list = findViewById(R.id.km_list);
        adapter = new ArrayAdapter<Double>(this,android.R.layout.simple_list_item_1,km);
        km_list.setAdapter(adapter);
        km_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Intent details = new Intent(MainActivity.this,order_details.class);
                orders_list.get(i).setCity(city);
                orders_list.get(i).setDate(currentDate);
                orders_list.get(i).setDelivery_type(delivery_type);
                details.putExtra("order",orders_list.get(i));
                startActivity(details);

            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    // خد الكود ده زى ماهو كده يا عمرو و حطه ف sign in  بالاضافه لشويه تعديلات
    DatabaseReference d_ref;
    ValueEventListener d_listen;
    @Override
    protected void onStart() {
        super.onStart();
        d_ref = FirebaseDatabase.getInstance().getReference().child("delivery").child("d_id").child("order");
        d_listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    order_data order = dataSnapshot.getValue(order_data.class);
                    Intent details = new Intent(MainActivity.this,order_details.class);
                    details.putExtra("order",order);
                    details.putExtra("have_order",true);
                    startActivity(details);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        d_ref.addValueEventListener(d_listen);
    }

    public void get_my_place(View view) {
        getLastLocation();

    }

    ValueEventListener listener ;
    DatabaseReference ref;

    private void get_useres_data() {


        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

         ref= FirebaseDatabase.getInstance().getReference().child("order").child(currentDate).child(city).child(delivery_type);

         listener = new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                orders_list .clear();
                km.clear();
                int i =0;
                for(DataSnapshot data : dataSnapshot.getChildren()){
                   order_data order =data.getValue(order_data.class);
                    float[] results = new float[1];
                    Location.distanceBetween(order.getS_lat(), order.getS_long(),
                            delivery_lat, delivery_long,
                            results);

                    order.setKM((double) results[0]/1000);
                    order.setOrder_id(data.getKey());
                    if(results[0]/1000 <= 7){
                        orders_list.add(order);
                        km.add(order.getKM());
                        adapter.notifyDataSetChanged();
                    }
                    i++;
                }

                /*
                if(i==dataSnapshot.getChildrenCount()){
                    // arrange order_list by km (kilo meter)
                    Collections.sort(orders_list, new Comparator<order_data>() {
                        @Override
                        public int compare(order_data o1, order_data o2) {
                            return o1.getKM().compareTo(o2.getKM());
                        }
                    });


                }
*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        ref.addValueEventListener(listener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(ref!=null)
        ref.removeEventListener(listener);
        if(d_ref!=null)
        d_ref.removeEventListener(d_listen);
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

                                    delivery_lat = location.getLatitude();
                                    delivery_long = location.getLongitude();
                                    get_delivery_city(new LatLng(location.getLatitude(),location.getLongitude()));

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
            delivery_lat = mLastLocation.getLatitude();
            delivery_long = mLastLocation.getLongitude();
            get_delivery_city(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

        }
    };

    private void get_delivery_city(LatLng position){

        try {
            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                city = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        get_useres_data();

    }




}
