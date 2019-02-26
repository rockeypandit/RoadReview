package com.example.roadreview;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends EasyLocationAppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    Double myLatitude, myLongitude;
    FirebaseUser currentuser;

    private ProgressDialog mProgressDialog;
    double lat, lng;
    Uri downloadUri;
    Uri uri;
    private DatabaseReference mDatabase, mDatabaseUser, mDatabaseList;
    String place;
    StorageReference imageRef;
    Geocoder geo;
    Bitmap photo;
    boolean gps_enabled;
    StorageReference filePath;
    private LocationManager locationManager;
    private LocationListener locationListener;
    Button sendButton;
    private Bitmap oldDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        sendButton = (Button) this.findViewById(R.id.send);
        oldDrawable = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        mProgressDialog = new ProgressDialog(this);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("User").child(currentuser.getUid()).push();
        mDatabaseList = FirebaseDatabase.getInstance().getReference().child("List").push();


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

        locationRequest();


        filePath = FirebaseStorage.getInstance().getReference().child("Images " + System.currentTimeMillis());


        Log.i("IMAGE", imageView.getDrawable().toString());

        final HashMap<String, Object> result = new HashMap<>();


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setMessage("UPLOADING");
                mProgressDialog.show();





                filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadUri = task.getResult();

                            Log.i("LINK " ,downloadUri.toString());

//                            FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUri.toString());
//                            mMessagesDatabaseReference.push().setValue(friendlyMessage);
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "UPLOAD SUCCESS", Toast.LENGTH_LONG).show();
                            result.put("Image",downloadUri.toString());
                            mDatabaseUser.setValue(result);
                            mDatabaseList.setValue(result);



                        } else {
                            Toast.makeText(MainActivity.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }


                });

                result.put("Image",downloadUri);
                result.put("lat", myLatitude);
                result.put("lng", myLongitude);


                mDatabaseUser.setValue(result);
                 //mDatabaseUser.push();
                mDatabaseList.setValue(result);





            }
        });


    }


    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location location) {
        String text = "try";
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
                // locationTextBox.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {

                    text = (addresses.get(0).getFeatureName() + "," + addresses.get(0).getLocality() + "," + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        //    System.out.println("coordinates:"+"lat="+location.getLatitude()+" lon="+location.getLongitude()+" accuracy="+location.getAccuracy());
        // Log.i("PLACE ",text);


    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }


    public void locationRequest() {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }


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
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            Bundle extras = data.getExtras();

            Bitmap imageBitmap = (Bitmap) extras.get("data");


            //data.getData();

//                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//                        String path = MediaStore.Images.Media.insertImage(MainActivity.this.getContentResolver(), photo, "Title", null);
//                        uri = Uri.parse(path);

          //  uri = getImageUri(getApplicationContext(), imageBitmap);
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            uri = tempUri;



            Log.i("URI", tempUri.toString());


            Bitmap newDrawable = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            if (newDrawable != oldDrawable) {
                sendButton.setVisibility(View.VISIBLE);
            }


        }
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }








//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }


}







