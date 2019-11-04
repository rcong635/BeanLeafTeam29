package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

//GeoFence Libraries
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/*
import android.app.PendingIntent;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import android.graphics.Color;
import android.Manifest;
import android.content.pm.PackageManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
*/

import java.util.ArrayList;
import java.util.Map;
//, OnCompleteListener<Void>
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    private GoogleMap mMap;
    private FirebaseFirestore db;
    private boolean addLocationButton;


    //GeoFencing DataMembers
    /*
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private GeofencingClient geofencingClient; //used to determine if we've enter coffee shop
    //GeoFence API - tracks the request status
    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }
    private PendingIntent GeofencePendingIntent; //used to add/remove fences
    private ArrayList<Geofence> mGeofenceList; //our application requires only 1 geofence
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
    // Draw Geofence circle on GoogleMap
    private Circle geoFenceLimits;
    double lat = 34.0224;
    double lon = -118.2851;
    float rad = 42;
    String reqId = "USC_Marker";
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        //GeoFence Initialization
        //mGeofenceList = new ArrayList<>();
        //GeofencePendingIntent = null;
        //geofencingClient = LocationServices.getGeofencingClient(this);

    }

    /*
    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            performPendingGeofenceTask();
        }
    }*/

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

        FirebaseUIActivity.openFbReference("some_data", this);
        if(FirebaseUIActivity.isUserLoggedIn()){
            Toast.makeText(this, "User is logged in", Toast.LENGTH_SHORT).show();
            FirebaseUIActivity.addUserToFirestore();
            FirebaseUIActivity.checkAdmin(this);
            displayLocations();
        } else {
            FirebaseUIActivity.attachListener();
        }


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
        switch (item.getItemId()) {
            case R.id.logout:
                //removeGeofences();
                FirebaseUIActivity.logout(this);
                return true;
            case R.id.Add:
                Intent intent = new Intent(this, AddLocActivity.class);
                startActivity(intent);
                return true;
            case R.id.View_History:
                //addGeofences();
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
                                mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
                            }
                        } else {
                            Log.d("Some string", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /*
    //Geofence creation process --> create Geofence --> create Request --> add request to geofence

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    //Create the Geofence Object
    @SuppressWarnings("MissingPermission")
    private void addGeofences() {

        //create Geofence
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(reqId)

                .setCircularRegion(
                        lat,
                        lon,
                        rad
                )
                .setExpirationDuration(3000) //expires after 30 seconds
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .setLoiteringDelay(3000) //check every 30 seconds
                .build());
        mPendingGeofenceTask = PendingGeofenceTask.ADD;
        //create Request

        //Add Request to GeoFence
        geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }


    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {
        mPendingGeofenceTask = PendingGeofenceTask.REMOVE;
        geofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    //use pending intent to handle geofence event
    private PendingIntent getGeofencePendingIntent() {
        Log.d(TAG, "createGeofencePendingIntent");
        // Reuse the PendingIntent if we already have it.
        if (GeofencePendingIntent != null) {
            return GeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        GeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return GeofencePendingIntent;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            if(mPendingGeofenceTask == PendingGeofenceTask.ADD){
                drawGeofence();
            }else
                geoFenceLimits.remove();

            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        mPendingGeofenceTask = PendingGeofenceTask.NONE; //clear it at the end
    }

    //For visual references -- visit our coffee shop location
    private void drawGeofence() {
        Log.d(TAG, "drawGeofence()");

        if ( geoFenceLimits != null )
            geoFenceLimits.remove();

        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat, lon))
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor( Color.argb(100, 150,150,150) )
                .radius( rad );
        geoFenceLimits = mMap.addCircle(circleOptions);
    }
    */
}
