package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Geocoder;
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


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

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

        displayLocations();


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
        popup.inflate(R.menu.user_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(MapsActivity.this, "Options-Select", Toast.LENGTH_LONG).show();
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
            case R.id.View_History:
                Toast.makeText(MapsActivity.this, "View_History", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                                mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
                            }
                        } else {
                            Log.d("Some string", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
