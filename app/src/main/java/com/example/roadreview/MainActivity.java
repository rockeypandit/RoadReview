package com.example.roadreview;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;

public class MainActivity extends EasyLocationAppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Double myLatitude,myLongitude;

    double lat, lng;

    private DatabaseReference mDatabase, mDatabaseUser, mDatabaseList;
    String place;
    Geocoder geo;

    boolean gps_enabled;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User");
        mDatabaseList = FirebaseDatabase.getInstance().getReference().child("List");


        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.0f, this);
//        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.ACCESS_FINE_LOCATION},1);


        }
        else {
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        }
      //  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);



//LOCATION OLD


//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
//
//
////            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
////                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//                geo = new Geocoder(this, Locale.getDefault());
//
//
//
//                GetLoc.requestSingleUpdate(this, new GetLoc.LocationCallback() {
//                    @Override public void onNewLocationAvailable(GetLoc.GPSCoordinates location) {
//                        lat = location.latitude;
//                        lng = location.longitude;
//                        Log.d("GetLocation", "LATITUDE " + location.latitude);
//                        Log.d("GetLocation", "LATITUDE " + location.longitude);
//
//                        //PLACE
//                        try {
//                            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
//
//
//                            if (addresses.isEmpty()) {
//                                place = "Waiting for GetLocation";
//                            } else {
//                                if (addresses.size() > 0) {
//                                    place = addresses.get(0).getFeatureName() + "," + addresses.get(0).getLocality() + "," + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
//                                }
//                            }
//
//                        }catch (Exception e){}
//                        Log.d("PLACE :",place);
//
//
//
//                    }
//                });
//
//
////            } else {
////                // do request the permission
////                Log.i("LOCATION","ERRORRR");
////                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8);
////                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 8);
////
////
////
////            }
//        }
//        else {
//                // do request the permission
//                Log.i("LOCATION","ERRORRR");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 8);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//
//
//            }





















        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });








}






    public void locationRequest(){
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(3000)
                .build();

        requestLocationUpdates(easyLocationRequest);
    }




//    locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(GetLocation location) {
//            Log.d("LOCATION", location.toString());
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//    };


            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
//                    //Start your code
//                } else {
//                    //Show snackbar
//                }


//                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//
//                }


                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                    }
                }







//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
//                    //Start your code
//                } else {
//                    Toast.makeText(this, "GPS permission denied", Toast.LENGTH_LONG).show();
//                }



                if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                        Intent cameraIntent = new
                                Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    } else {
                        Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                    }

                }










            }


                protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        imageView.setImageBitmap(photo);
                    }
                }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        Log.i("latitude",myLatitude.toString());
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }
}







