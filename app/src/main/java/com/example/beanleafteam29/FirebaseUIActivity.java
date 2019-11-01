package com.example.beanleafteam29;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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
    private static FirebaseFirestore db;

    private FirebaseUIActivity() {

    }
    public static boolean isAdmin;

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
                        //checkAdmin(userId);
                    }
                    FirebaseUIActivity.detachListener();
                }
            };
        }
    }

    public static boolean isUserLoggedIn() {
        if(firebaseUtil != null) {
            if(mFirebaseAuth != null) {
                if(mFirebaseAuth.getCurrentUser() != null)
                    return true;
            }
        }
        return false;
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
}