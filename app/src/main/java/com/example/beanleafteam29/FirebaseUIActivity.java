package com.example.beanleafteam29;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import android.location.Address;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class FirebaseUIActivity {
    private static FirebaseUIActivity firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static MapsActivity caller;
    private static boolean isAdmin = false;
    private static FirebaseFirestore db;
    private static HashMap<String, Object> userLocations = new HashMap<>();
    private static HashMap<String, Object> allLocations = new HashMap<>();
    private static long caffeineAmount = 0;
    private static boolean newSignIn = false;
    private static  List<Map<String, Object> > menu = new ArrayList<>();
    private static HashMap<String, Object> userHistory = new HashMap<>();
    private static String username = "";

    public FirebaseUIActivity() {

    }

    public static boolean getIsAdmin() {
        return FirebaseUIActivity.isAdmin;
    }

    public static boolean getNewSignIn() {
        return FirebaseUIActivity.newSignIn;
    }

    public static void setNewSignIn(boolean value) {
        FirebaseUIActivity.newSignIn = false;
    }

    public static void openFbReference(String ref, final MapsActivity callerActivity) {
        //Toast.makeText(callerActivity.getBaseContext(), "openFbReference() called", Toast.LENGTH_SHORT).show();
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUIActivity();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    //Toast.makeText(callerActivity.getBaseContext(), "Auth state changed!!", Toast.LENGTH_SHORT).show();
                    newSignIn = true;
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUIActivity.signIn(callerActivity);
                    } else {
                        String userId = firebaseAuth.getUid();
                        //Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
                    }
                    FirebaseUIActivity.detachListener();
                }
            };
        }
    }

    public static void setCaffeineLocal(long amount) {
        FirebaseUIActivity.caffeineAmount = amount;
    }

    public static void computeCaffeineAmount() {
        db = FirebaseFirestore.getInstance();
        final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        final String uid = getUid();
        db.collection("Users")
                .document(uid)
                .collection("History")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            FirebaseUIActivity.caffeineAmount = 0;
                            if(task.getResult().size() != 0) {
                                for(QueryDocumentSnapshot document : task.getResult()) {
                                    Date queryDate = document.getTimestamp("Date").toDate();
                                    if(fmt.format(queryDate).equals(fmt.format(new Date()))) {
                                        FirebaseUIActivity.caffeineAmount += document.getLong("Caffeine");
                                    }
                                }
                            }
                        } else {
                            //Toast.makeText(callerActivity.getBaseContext(), "Task is NOT successful!", Toast.LENGTH_SHORT).show();
                            Log.d("checkAdmin", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static long getCaffeineAmount() {
        return FirebaseUIActivity.caffeineAmount;
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

    // call this function in onCreate() of MapsActivity
    public static void getUsernameFb() {
        db = FirebaseFirestore.getInstance();
        final String uid = getUid();
        db.collection("Users")
                .whereEqualTo("UID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().size() != 0) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    username = document.getString("Name");
                                }
                            }
                        } else {
                            Log.d("getUsernameFb", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
    public static String getUsername() {
        return username;
    }


    public static void logout(final MapsActivity callerActivity) {
        //Toast.makeText(callerActivity.getBaseContext(), "logout() called", Toast.LENGTH_SHORT).show();
        AuthUI.getInstance().signOut(callerActivity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Logout", "User logged out");
                        FirebaseUIActivity.attachListener();
                    }
                });
    }

//    public static void addLocation(String locationName, String address) {
//        Context myContext = MapsActivity.getAppContext();
//        Geocoder coder = new Geocoder(myContext);
//        double latitude = 0, longitude = 0;
//        try {
//            ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(address, 5);
//            for(Address add : adresses){
//                longitude = add.getLongitude();
//                latitude = add.getLatitude();
//            }
//        } catch(IOException ex) {
//            System.err.println (ex.toString());
//            System.err.println("IOException thrown in method addLocation()");
//            System.exit(1);
//        }
//        Map<String, Object> locations = new HashMap<>();
//        GeoPoint latLong = new GeoPoint(latitude, longitude);
//        locations.put("Coordinates", latLong);
//        locations.put("Name", locationName);
//        locations.put("Owner", getUid());
//        locations.put("Verified", false);
//        db = FirebaseFirestore.getInstance();
//        db.collection("Locations").document()
//                .set(locations)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("FirebaseUIActivity", "DocumentSnapshot successfully written!");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w("FirebaseUIActivity", "Error writing document", e);
//                    }
//                });
//    }

    public static void makeAdmin() {
        if (isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            final String uid = getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("Admin", true);
            db.collection("Users")
                    .document(uid)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("FirebaseUIActivity", "DocumentSnapshot successfully written!");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("FirebaseUIActivity", "Error writing document", e); }
            });
        }
        FirebaseUIActivity.isAdmin = true;
        caller.showAddLocationButton();
    }

    public static void makeNonAdmin() {
        if (isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            final String uid = getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("Admin", false);
            db.collection("Users")
                    .document(uid)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("FirebaseUIActivity", "DocumentSnapshot successfully written!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("FirebaseUIActivity", "Error writing document", e); }
            });
        }
        FirebaseUIActivity.isAdmin = false;
        caller.hideAddLocationButton();
    }

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
                                callerActivity.showAddLocationButton();
                                //Toast.makeText(callerActivity.getBaseContext(), "User is admin", Toast.LENGTH_SHORT).show();
                            } else {
                                FirebaseUIActivity.isAdmin = false;
                                callerActivity.hideAddLocationButton();
                                //Toast.makeText(callerActivity.getBaseContext(), "User is NOT admin", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //Toast.makeText(callerActivity.getBaseContext(), "Task is NOT successful!", Toast.LENGTH_SHORT).show();
                            Log.d("checkAdmin", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static HashMap<String, Object> getUserLocations() {
        return userLocations;
    }

    public static String getUserName() {
        return mFirebaseAuth.getCurrentUser().getDisplayName();
    }

    public static void queryDatabaseForCurrentUserLocations() {
        if (isUserLoggedIn()) {
            userLocations.clear();
            final String uid = getUid();
            db = FirebaseFirestore.getInstance();
            db.collection("Locations")
                    //.whereEqualTo("Owner", getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() != 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String ownerUid = document.getString("Owner");
                                        if (ownerUid.equals(uid))
                                            userLocations.put(document.getId(), document.getData());
                                    }
                                }
                            }
                        }
                    });
        }
    }

    public static void queryDatabaseForAllLocations() {
        allLocations.clear();
        db = FirebaseFirestore.getInstance();
        db.collection("Locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                allLocations.put(document.getId(), document.getData());
                            }
                        }
                    }
                });
    }

    public static boolean isStoreVerified(String storeID) {
        if (allLocations.size() != 0) {
            HashMap<String, Object> myDocument = null;
            for (Map.Entry mapElement : allLocations.entrySet()) {
                if (storeID.equals(mapElement.getKey())) {
                    myDocument = (HashMap) mapElement.getValue();
                    break;
                }
            }
            return (boolean) myDocument.get("Verified");
        } else {
            throw new RuntimeException();
        }
    }

    public static HashMap<String, Object> getAllLocations() {
        return allLocations;
    }

    public static void setStoreVerified(String storeID, boolean verified) {
        // ALSO UPDATE LOCAL VARIABLE WHEN ADDING A STORE TO THE DATABASE
        HashMap<String, Object> myLocation = (HashMap<String, Object>) allLocations.get(storeID);
        myLocation.put("Verified", verified); // update local variable
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("Verified", verified);
        db.collection("Locations")
                .document(storeID)
                .set(data, SetOptions.merge());
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    private static void signIn(final MapsActivity callerActivity) {
        //Toast.makeText(callerActivity.getBaseContext(), "signIn() called", Toast.LENGTH_SHORT).show();
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

    public static void addElementToUserHistory(HashMap<String, Object> m) {
        final HashMap<String, Object> myOrder = (HashMap<String, Object>) m.clone();
        if(isUserLoggedIn()) {
            String uid = mFirebaseAuth.getUid();
            //String id = db.collection("Users").document("History").collection("History").getId();
            db = FirebaseFirestore.getInstance();
            db.collection("Users")
                    .document(uid)
                    .collection("History")
                    .add(m)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    setUserHistoryLocal(documentReference.getId(), myOrder);
                    //Log.d("addElementToUserHistory", "DocumentSnapshot written with ID: " + documentReference.getId());
                }
            });
        }
    }

    public static void addElementToLocationHistory(Map m, String locationId) {
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            String uid = mFirebaseAuth.getUid();
            db.collection("Locations")
                    .document(locationId)
                    .collection("History")
                    .add(m);
        }
    }

    public static void addElementToMenu(Map m, String locationId) {
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            db.collection("Locations")
                    .document(locationId)
                    .collection("Menu")
                    .add(m);
        }
    }

    //used to initialize the menu items
    public static void getLocationMenuFb(String locationId) {
        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Locations/" + locationId + "/Menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                menu.clear();
                                if(task.getResult().size() != 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> myData = document.getData();
                                        menu.add(myData);
                                    }
                                }
                            } else {
                                Log.d("getLocMenu", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public static List<Map<String, Object> > getLocationMenu() {
        return menu;
    }

    public static void deleteElementFromMenu(final String locationId, String itemToBeDeleted) {
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            // query database for itemToBeDeleted first and get the documentId
            // call delete on documentId
            db.collection("Locations")
                    .document(locationId)
                    .collection("Menu")
                    .whereEqualTo("Name", itemToBeDeleted)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() == 1) { // there should only be 1 element with that name
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String documentId = document.getId();
                                        db.collection("Locations")
                                                .document(locationId)
                                                .collection("Menu")
                                                .document(documentId)
                                                .delete();
                                    }
                                }
                            } else {
                                Log.d("deleteElementFromMenu", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    // deletes user that is currently logged in from Firebase Authentication
    public static void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("deleteUser", "User account deleted.");
                        }
                    }
                });

        String uid = mFirebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("deleteUser", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("deleteUser", "Error deleting document", e);
                    }
                });
    }

    public static void getUserHistoryFb() {
        if(isUserLoggedIn()) {
            db = FirebaseFirestore.getInstance();
            String uid = mFirebaseAuth.getUid();
            db.collection("Users/" + uid + "/History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                userHistory.clear();
                                if(task.getResult().size() != 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        userHistory.put(document.getId(), document.getData());
                                    }
                                }
                            } else {
                                Log.d("getUserHistory", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public static HashMap<String, Object> getUserHistory() {
        return userHistory;
    }

    public static void setUserHistoryLocal(String key, HashMap<String, Object> doc) {
        userHistory.put(key, doc);
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
                                }
                            } else {
                                Log.d("addUserToFirestore", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public static void addLocnMenu(String locationName, String address, List<Map<String, Object>> menu, final AddLocActivity callerActivity) {
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
        locations.put("Owner", getUid());
        locations.put("Verified", false);
        db = FirebaseFirestore.getInstance();
        String id = db.collection("Locations").document().getId();
        allLocations.put(id, locations); // todo add menu to local variable too
        userLocations.put(id, locations);
        db.collection("Locations").document(id)
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
        for (Map<String, Object> map : menu) {
            db.collection("Locations")
                    .document(id)
                    .collection("Menu")
                    .add(map);
        }
    }
}