package com.example.beanleafteam29;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.location.Address;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUIActivity {
    private static FirebaseUIActivity firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static MapsActivity caller;
    private static boolean isAdmin = false;
    private static FirebaseFirestore db;

    public FirebaseUIActivity() {

    }


    public static void openFbReference(String ref, final MapsActivity callerActivity) {
        Toast.makeText(callerActivity.getBaseContext(), "openFbReference() called", Toast.LENGTH_SHORT).show();
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUIActivity();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUIActivity.signIn(callerActivity);
                    } else {
                        String userId = firebaseAuth.getUid();
                        Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUIActivity.detachListener();
                    //checkAdmin(callerActivity);
                }
            };
        }
    }


    public static boolean isUserLoggedIn() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            return true;
        return false;
    }

    public static String getUid() {
        if(mFirebaseAuth != null)
            return mFirebaseAuth.getUid();
        return null;
    }

    public static void logout(final MapsActivity callerActivity) {
        Toast.makeText(callerActivity.getBaseContext(), "logout() called", Toast.LENGTH_SHORT).show();
        AuthUI.getInstance().signOut(callerActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout", "User logged out");
                        FirebaseUIActivity.attachListener();
                    }
                });

    }

    public static void addLocation(String locationName, String address, final AddLocActivity callerActivity) {
        Geocoder coder = new Geocoder(callerActivity);
        double latitude = 0, longitude = 0;
        try {
            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(address, 5);
            for(Address add : adresses){
                longitude = add.getLongitude();
                latitude = add.getLatitude();
            }
        } catch(IOException ex) {
            System.err.println (ex.toString());
            System.err.println("IOException thrown in method addLocation()");
            System.exit(1);
        }
        Map<String, Object> locations = new HashMap<>();
        GeoPoint latLong = new GeoPoint(latitude, longitude);
        locations.put("Coordinates", latLong);
        locations.put("Name", locationName);
        db = FirebaseFirestore.getInstance();
        db.collection("Locations").document()
                .set(locations)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("FirebaseUIActivity", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("FirebaseUIActivity", "Error writing document", e);
                    }
                });
    }


    // check admin does not work
    public static void checkAdmin(final MapsActivity callerActivity) {
        db = FirebaseFirestore.getInstance();
        final String userID = FirebaseUIActivity.getUid();
        db.collection("Users")
                .whereEqualTo("UID", userID)
                .whereEqualTo("Admin", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() != 0) {
                                FirebaseUIActivity.isAdmin = true;
                                callerActivity.showButton();
                                //Toast.makeText(callerActivity.getBaseContext(), "User is admin", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUIActivity.isAdmin = false;
                                callerActivity.hideButton();
                                //Toast.makeText(callerActivity.getBaseContext(), "User is NOT admin", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(callerActivity.getBaseContext(), "Task is NOT successful!", Toast.LENGTH_SHORT).show();
                            Log.d("checkAdmin", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    private static void signIn(final MapsActivity callerActivity) {
        Toast.makeText(callerActivity.getBaseContext(), "signIn() called", Toast.LENGTH_SHORT).show();
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    public static void getUserHistory() {
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            String uid = mFirebaseAuth.getUid();
            db.collection("Users/" + uid + "/History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() != 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> myData = document.getData();
                                        System.out.println(document);
                                        System.out.println(task.getResult().size());
                                    }

                                }
                            } else {
                                Log.d("checkAdmin", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public static void addUserToFirestore() {
        //Toast.makeText(caller.getBaseContext(), "addUser is called!", Toast.LENGTH_SHORT).show();
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            String uid = mFirebaseAuth.getUid();
            db.collection("Users")
                    .whereEqualTo("UID", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() == 0) {
                                    String name = mFirebaseAuth.getCurrentUser().getDisplayName();
                                    String email = mFirebaseAuth.getCurrentUser().getEmail();
                                    String uid = mFirebaseAuth.getUid();
                                    boolean admin = false;
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("Name", name);
                                    data.put("Email", email);
                                    data.put("Admin", admin);
                                    data.put("UID", uid);
                                    db.collection("Users").document(uid)
                                            .set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("FirebaseUIActivity", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("FirebaseUIActivity", "Error writing document", e);
                                                }
                                            });
                                    data.clear();
                                    db.collection("Users")
                                            .document(uid)
                                            .collection("History")
                                            .document()
                                            .set(data);
                                }
                            } else {
                                Log.d("checkAdmin", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}