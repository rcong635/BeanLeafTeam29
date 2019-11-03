package com.example.beanleafteam29;

import com.google.android.gms.maps.CameraUpdate;
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
import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener, PopupMenu.OnMenuItemClickListener, OnMarkerClickListener {

    private HashMap<String, Marker> locIdToMarker = new HashMap<>();
    private HashMap<Marker, String> markerToLocId = new HashMap<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private boolean addLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "onMapReady() called", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        LatLng location = new LatLng(400, -118);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        enableMyLocation();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } catch (SecurityException se) {

        }

        mMap.setMinZoomPreference(16);
        //mMap.setOnMarkerClickListener(this);
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

        FirebaseUIActivity.openFbReference("some_data", this);
        if(FirebaseUIActivity.isUserLoggedIn()) {
            Toast.makeText(this, "User is logged in", Toast.LENGTH_SHORT).show();
            FirebaseUIActivity.addUserToFirestore();
            FirebaseUIActivity.checkAdmin(this);
            displayLocations();
            FirebaseUIActivity.getUserHistory();
        } else {
            FirebaseUIActivity.attachListener();
        }


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent intent3 = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(intent3);
        Toast.makeText(getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
        return false;
    }


    @Override
    protected void onResume() {
        //Toast.makeText(this, "onResume() called", Toast.LENGTH_SHORT).show();
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        //Toast.makeText(this, "onPause() called", Toast.LENGTH_SHORT).show();
        super.onPause();
        FirebaseUIActivity.detachListener();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        if(addLocationButton)
            popup.inflate(R.menu.admin_menu);
        else
            popup.inflate(R.menu.user_menu);
        popup.show();
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionsUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //Toast.makeText(MapsActivity.this, "Options-Select", Toast.LENGTH_LONG).show();
        switch (item.getItemId()) {
            case R.id.logout:
                Toast.makeText(MapsActivity.this, "Logout-Menu", Toast.LENGTH_LONG).show();
                FirebaseUIActivity.logout(this);
                return true;
            case R.id.Add:
                Toast.makeText(MapsActivity.this, "Add-Menu", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, AddLocActivity.class);
                startActivity(intent);
                return true;
/*            case R.id.profileBtn:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideButton() {
        addLocationButton = false;
    }

    public void showButton() {
        addLocationButton = true;
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionsUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //currentLocation = latLng;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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