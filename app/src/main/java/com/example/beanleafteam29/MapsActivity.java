package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Location;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
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
        Toast.makeText(this, "onMapReady() called", Toast.LENGTH_SHORT).show();

        mMap = googleMap;
        LatLng USC = new LatLng(34.0224, -118.2851);
        mMap.addMarker(new MarkerOptions().position(USC).title("Marker at USC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(USC));
        mMap.setMinZoomPreference(16);

        db = FirebaseFirestore.getInstance();
        db.collection("Locations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String location_name = document.getString("Name");
                                GeoPoint coordinates = document.getGeoPoint("Coordinates");
                                LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
                            }
                        } else {
                            Log.d("Some string", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        Toast.makeText(this, "onResume() called", Toast.LENGTH_SHORT).show();
        super.onResume();
        if(!FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseUIActivity.openFbReference("some_data", this);
            FirebaseUIActivity.attachListener();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button b1= findViewById(R.id.logout);
    }

    @Override
    protected void onPause() {
        Toast.makeText(this, "onPause() called", Toast.LENGTH_SHORT).show();
        super.onPause();
        FirebaseUIActivity.detachListener();
    }

    public void onClick(View v) {
        Toast.makeText(this, "onClick() called", Toast.LENGTH_SHORT).show();
        switch (v.getId()) {
            case R.id.logout:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User logged out");
                                FirebaseUIActivity.attachListener();
                            }
                        });
                break;
            case R.id.Add:

                Intent intent = new Intent(this, AddLocActivity.class);
                startActivity(intent);

                break;
            default:
                Toast.makeText(MapsActivity.this, "default", Toast.LENGTH_LONG).show();
                break;
        }
    }


}
