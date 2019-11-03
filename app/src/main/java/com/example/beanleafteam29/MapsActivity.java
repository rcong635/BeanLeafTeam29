package com.example.beanleafteam29;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

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


public class MapsActivity extends FragmentActivity implements OnMarkerClickListener, OnMapReadyCallback {
    private HashMap<String, Marker> locIdToMarker = new HashMap<>();
    private HashMap<Marker, String> markerToLocId = new HashMap<>();

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "onMapReady() called", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        LatLng USC = new LatLng(34.0224, -118.2851);
        Marker m1 = mMap.addMarker(new MarkerOptions().position(USC).title("Marker at USC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(USC));
        mMap.setMinZoomPreference(16);
        mMap.setOnMarkerClickListener(this);

        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        BottomPanel bottomSheet = null;
                        if (markerToLocId.containsKey(marker)) {
                            bottomSheet = new BottomPanel(markerToLocId.get(marker));
                        }
                        else {
                            bottomSheet = new BottomPanel("didnt find it");

                        }
                        bottomSheet.show(getSupportFragmentManager(), marker.getTitle());

                        return false;
                    }
                }


        );

        displayLocations();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent intent3 = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(intent3);
        Toast.makeText(getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
//

        return false;
    }


    @Override
    protected void onResume() {
        Toast.makeText(this, "onResume() called", Toast.LENGTH_SHORT).show();
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(!FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseUIActivity.openFbReference("some_data", this);
            FirebaseUIActivity.attachListener();
        }
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
                FirebaseUIActivity.logout(this);
                break;
            case R.id.Add:
                Intent intent = new Intent(this, AddLocActivity.class);
                startActivity(intent);
                break;
            case R.id.profileBtn:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                break;
            default:
                Toast.makeText(MapsActivity.this, "default", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void displayLocations() {
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
                                Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
                                locIdToMarker.put(document.getId(), newMarker);
                                markerToLocId.put(newMarker, document.getId());
                            }
                        } else {
                            Log.d("Some string", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



}
