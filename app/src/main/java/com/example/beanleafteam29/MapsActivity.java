package com.example.beanleafteam29;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener, PopupMenu.OnMenuItemClickListener, OnMarkerClickListener {

    //hash maps for keeping track of markers on added onto the map
    private HashMap<String, Marker> locIdToMarker = new HashMap<>();
    private HashMap<Marker, String> markerToLocId = new HashMap<>();

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private boolean addLocationButton;

//    Request code for location permission request.
//    @see #onRequestPermissionsResult(int, String[], int[])

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


//    Flag indicating whether a requested permission has been denied after returning in
//    {@link #onRequestPermissionsResult(int, String[], int[])}.
    private boolean mPermissionDenied = false;

    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        FirebaseUIActivity.openFbReference("some_data", this);
        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseUIActivity.addUserToFirestore();
            FirebaseUIActivity.checkAdmin(this);
            displayLocations();
        } else {
            FirebaseUIActivity.attachListener();
        }
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

        if (FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseUIActivity.addUserToFirestore();
            FirebaseUIActivity.checkAdmin(this);
            displayLocations();
        }

//      Creating listeners for markers on the map to show their data
//        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerClickListener( new GoogleMap.OnMarkerClickListener() {
               @Override
               public boolean onMarkerClick(Marker marker) {
                   BottomPanel bottomSheet = null;
                   if (markerToLocId.containsKey(marker)) {
                       bottomSheet = new BottomPanel(marker.getTitle(), markerToLocId.get(marker));
                   }
                   else {
                       bottomSheet = new BottomPanel("Ooops, sorry!", "");

                   }
                   bottomSheet.show(getSupportFragmentManager(), marker.getTitle());

                   return false;
               }
           }
        );

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent intent3 = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(intent3);
        //Toast.makeText(getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //Toast.makeText(MapsActivity.this, "Options-Select", Toast.LENGTH_LONG).show();
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseUIActivity.logout(this);
                return true;

            case R.id.Add:
                //Toast.makeText(MapsActivity.this, "Add-Menu", Toast.LENGTH_LONG).show();
                Intent addIntent = new Intent(this, AddLocActivity.class);
                startActivity(addIntent);
                return true;

/*            case R.id.profileBtn:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                return true;*/

            case R.id.View_History:
                //Toast.makeText(MapsActivity.this, "View_History", Toast.LENGTH_LONG).show();
                Intent historyIntent = new Intent(this, UserHistoryActivity.class);
                startActivity(historyIntent);
                return true;

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionsUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        String origin = latLngToString(currentLocation);
//        String destination = latLngToString(marker.getPosition());
//        displayRoute(origin, destination);
//        return false;
//    }
//
//    private String latLngToString(LatLng location) {
//        String latLng = location.toString();
//        latLng = latLng.substring(10);
//        latLng = latLng.substring(0, latLng.length() - 1);
//        return latLng;
//    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionsUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

//    private GeoApiContext getGeoContext() {
//        GeoApiContext geoApiContext = new GeoApiContext();
//        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key))
//                .setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS)
//                .setWriteTimeout(1, TimeUnit.SECONDS);
//    }

//    private void addMarkersToMap(DirectionsResult results, GoogleMap map) {
//        map.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress));
//        map.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,results.routes[0].legs[0].endLocation.lng)).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
//    }

//    private void displayRoute(String origin, String destination) {
//        DateTime now = new DateTime();
//        try {
//            DirectionsResult result = DirectionsApi.newRequest(getGeoContext()).mode(TravelMode.DRIVING).origin(origin).destination(destination).departureTime(now).await();
//            //addMarkersToMap(result, mMap);
//            getEndLocationTitle(result);
//            addPolyline(result, mMap);
//        } catch (ApiException ae) {
//
//        } catch (InterruptedException ie) {
//
//        } catch (IOException ioe) {
//
//        }
//    }

//    private String getEndLocationTitle(DirectionsResult results){
//        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
//    }
//
//    private void addPolyline(DirectionsResult results, GoogleMap map) {
//        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
//        map.addPolyline(new PolylineOptions().addAll(decodedPath));
//    }

}
