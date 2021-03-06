package com.example.roadreview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private final static int RC_SIGN_IN = 2;
    SignInButton googBtn;
    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthListener;

    GoogleApiClient mGoogleApiClient;

            public void gotoMain() {

                // MADE CHANGES
                Intent i = new Intent(getApplicationContext(), MainActivity.class);


                startActivity(i);
                finish();
            }


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_login);

                googBtn = findViewById(R.id.googBtn);
                mAuth = FirebaseAuth.getInstance();

                mAuthListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            gotoMain();
                        } else {
//                    Intent gotoMainActivity = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(gotoMainActivity);
//                finish();
                        }
                    }
                };

//        if (mAuth !=null){
//            Intent gotoMainActivity = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(gotoMainActivity);
//                finish();
//        }


                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("929335893465-5nqdducr8eve0bmbkbnmu77949qqirj9.apps.googleusercontent.com")
                        .requestEmail()
                        .build();


                mGoogleApiClient = new GoogleApiClient.Builder(this).
                        enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.i("coneectoion result", connectionResult.toString());
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


                googBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        signIn();
                    }
                });


            }


            private void signIn() {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    // Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    if (result.isSuccess()) {
                        GoogleSignInAccount account = result.getSignInAccount();
                        firebaseAuthWithGoogle(account);
                    } else {
                        Toast.makeText(getApplicationContext(), "AUTH WENT WRONG", Toast.LENGTH_LONG).show();
                        Log.i("auth ",result.toString());
                    }


//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                firebaseAuthWithGoogle(account);
//            } catch (ApiException e) {
//                // Google Sign In failed, update UI appropriately
//              //  Log.w(TAG, "Google sign in failed", e);
//                // ...
//            }
                }
            }

            private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    gotoMain();
                                    Toast.makeText(getApplicationContext(), "LOGGED IN SUCESSFULLY", Toast.LENGTH_LONG).show();

                                    //  updateUI(user);
                                } else {


                                    Toast.makeText(getApplicationContext(), "FAILED TO LOG IN ", Toast.LENGTH_LONG).show();


                                }


                            }
                        });


            }


        }







//        FirebaseApp.initializeApp(this);








