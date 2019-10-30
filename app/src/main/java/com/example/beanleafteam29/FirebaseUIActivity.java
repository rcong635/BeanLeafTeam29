package com.example.beanleafteam29;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FirebaseUIActivity {
    private static FirebaseUIActivity firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static MapsActivity caller;

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
                }
            };
        }
    }

    public static boolean isUserLoggedIn() {
        if (firebaseUtil != null) return true;
        return false;
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