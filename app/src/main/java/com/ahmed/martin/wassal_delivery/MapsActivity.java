package com.ahmed.martin.wassal_delivery;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;



import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Double s_lat,s_long,r_lat,r_long,d_lat,d_long;
    private user_data user_details;
    private String type,address, fromsignup, fromedit;
    private Double latitude , longitude;
    private MarkerOptions marker;
    private boolean select_address;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        s_lat = getIntent().getDoubleExtra("s_lat",0);
        s_long = getIntent().getDoubleExtra("s_long",0);
        r_lat = getIntent().getDoubleExtra("r_lat",0);
        r_long = getIntent().getDoubleExtra("r_long",0);
        d_lat = getIntent().getDoubleExtra("d_lat",0);
        d_long = getIntent().getDoubleExtra("d_long",0);
        user_details =(user_data) getIntent().getSerializableExtra("user");
        fromsignup = getIntent().getStringExtra("signup");
        fromedit = getIntent().getStringExtra("edit");




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(fromsignup.equals("yes")|| fromedit.equals("yes")){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    latitude = latLng.latitude;
                    longitude = latLng.longitude;
                    Geocoder geo =new Geocoder(MapsActivity.this);
                    List<Address> list =new ArrayList<>();
                    try {
                        list=geo.getFromLocation(latitude,longitude,1);
                    } catch (IOException e) {

                    }
                    if(list.size()>0) {
                        mMap.clear();
                        address = list.get(0).getAddressLine(0);
                        marker = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title(address);
                        mMap.addMarker(marker);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        select_address = true;
                    }else{
                        Toast.makeText(MapsActivity.this,"error",Toast.LENGTH_LONG).show();
                    }
                }
            });
            LatLng sydney = new LatLng(30.0440680, 31.2355120);
            MarkerOptions mar = new MarkerOptions().position(sydney);
            mMap.addMarker(mar);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18));


        }else {

            // sender
            LatLng s_latLng = new LatLng(s_lat, s_long);
            MarkerOptions s_marker = new MarkerOptions().position(s_latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title("Sender Location");
            mMap.addMarker(s_marker);
            // receiver
            LatLng r_latLng = new LatLng(r_lat, r_long);
            MarkerOptions r_marker = new MarkerOptions().position(r_latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title("Sender Location");
            mMap.addMarker(r_marker);
            //delivery
            LatLng d_latLng = new LatLng(d_lat, d_long);
            MarkerOptions d_marker = new MarkerOptions().position(d_latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.location)).title("Sender Location");
            mMap.addMarker(d_marker);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(d_latLng, 15));
        }


//        LatLng origin = new LatLng(s_lat,s_long);
//        LatLng dest = new LatLng(r_lat,r_long);
//        // draw line between sender and receiver
//        // Getting URL to the Google Directions API
//        String url = getDirectionsUrl(origin, dest);
//
//        DownloadTask downloadTask_s_r = new DownloadTask();
//
//        // Start downloading json data from Google Directions API
//        downloadTask_s_r.execute(url);
//
//
//        // draw line between sender and delivery
//        dest = new LatLng(d_lat,d_long);
//        url = getDirectionsUrl(origin, dest);
//
//        DownloadTask downloadTask_s_d = new DownloadTask();
//
//        // Start downloading json data from Google Directions API
//        downloadTask_s_d.execute(url);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Toast.makeText(MapsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }



    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }



        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLACK);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }

    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key="+"AIzaSyBnL5s35NWzkAjkGxez5KLaPsaSHWb-SbY";//+getResources().getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    public void my_location(View view) {
        // location();
    }

    public void select_address(View view) {
        if(select_address) {
            if(fromsignup.equals("yes")) {
                user_details.setAddress(address);
                user_details.setAddress_lat(latitude);
                user_details.setAddress_long(longitude);
                Intent signup = new Intent(MapsActivity.this, sign_up.class);
                signup.putExtra("user", user_details);
                signup.putExtra("finish", true);
                signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(signup);
            }else{
                user_details.setAddress(address);
                user_details.setAddress_lat(latitude);
                user_details.setAddress_long(longitude);
                Intent signup = new Intent(MapsActivity.this, EditUserInfoActivity.class);
                signup.putExtra("user", user_details);
                signup.putExtra("finish", true);
                signup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                startActivity(signup);
            }
        }
        else
            Toast.makeText(MapsActivity.this, "sorry must select location to continue", Toast.LENGTH_LONG).show();

    }

}
