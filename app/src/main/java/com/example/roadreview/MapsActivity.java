package com.example.roadreview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;
    Double lng, lat;
    List Ulat,Ulng;
    FirebaseUser currentuser;
    Handler handler;
    LatLng marker;

    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(currentuser.getUid());
        handler = new Handler();


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Ulat = new ArrayList<String>();
                Ulng = new ArrayList<String>();
                String[] latArray, lngArray;
                DataSnapshot d1;


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //Iterable<DataSnapshot> child1 = child.getChildren();
                    // Log.i("LAT", child.toString());

                    // for (DataSnapshot contact : child.getChildren()) {
                    // Ulat.add(contact.child("lat").getValue());
//                        Log.i("LAT", child.child("lat").getValue().toString());
//                        Log.i("LNG", child.child("lng").getValue().toString());


                    Ulat.add((child.child("lat").getValue()));// "-key1", "-key2", etc
                    Ulng.add((child.child("lng").getValue()));


                    // Log.d("contact:: ", c.name + " " + c.phone);
                    // contacts.add(c);

                    //}
                }


//                    for (DataSnapshot child1 : child.getChildren()) {
//                        for (DataSnapshot child2 : child.getChildren()) {
//
//                            Ulat.add((child2.child("lat").getValue()));// "-key1", "-key2", etc
//                            Ulng.add((child2.child("lng").getValue()));
//                            // System.out.println(child.child("lng").getValue());
//                        }
//                    }

//                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
//                    Userlist.add(String.valueOf(dsp.getValue())); //add result into array list
//                }
//                for (int i = 0 ;i<Ulat.size();i++){
//                    Log.i("Lat ",Ulat.get(i).toString());
//                    Log.i("Lng ",Ulng.get(i).toString());
//
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });







       // mPostReference.addValueEventListener(postListener);











        String slat = getIntent().getExtras().getString("lat");
        String slng = getIntent().getExtras().getString("lng");

        lat = Double.parseDouble(slat);
        lng = Double.parseDouble(slng);

//        Log.i("LAT" , lat.toString());
//        Log.i("Lng" , lng.toString());




//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//         criteria= new Criteria();
//






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

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//        if (location != null)
//        {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
//
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
//                    .zoom(17)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
//                    .build();                   // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }

        // Add a marker in Sydney and move the camera


        LatLng currentLocation = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,12.0f));


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<Ulat.size();i++)
                {
                    Log.i("LATT",Ulat.get(i).toString());
                    marker= new LatLng(Double.parseDouble(Ulat.get(i).toString()),Double.parseDouble(Ulng.get(i).toString()));
                    mMap.addMarker(new MarkerOptions().position(marker).title("PotHole"));

                }

            }
        },5000);


//        for(int i=0;i<Ulat.size();i++)
//        {
//            LatLng marker = new LatLng(Double.parseDouble(Ulat.get(i).toString()),Double.parseDouble(Ulng.get(i).toString()));
//            mMap.addMarker(new MarkerOptions().position(marker).title("Marker"));
//
//        }



    }

    public void onBackPressed(){
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
