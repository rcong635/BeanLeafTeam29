package com.example.beanleafteam29;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import android.content.Context;
import android.widget.Toast;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        LocationListener, PopupMenu.OnMenuItemClickListener, OnMarkerClickListener {

    //hash maps for keeping track of markers on added onto the map
    private HashMap<String, Marker> locIdToMarker = new HashMap<>();
    private HashMap<Marker, String> markerToLocId = new HashMap<>();
    private HashMap<Marker, String> markerToName = new HashMap<>();
    private HashMap<Marker, GeoPoint> markerToCoord = new HashMap<>();

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
    private static Context context;

    private String provider;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        MapsActivity.context = getApplicationContext();

        FirebaseUIActivity.openFbReference("some_data", this);
        if (FirebaseUIActivity.isUserLoggedIn()) {
            displayLocations();
            FirebaseUIActivity.queryDatabaseForCurrentUserLocations();
            FirebaseUIActivity.addUserToFirestore();
            FirebaseUIActivity.checkAdmin(this);
            FirebaseUIActivity.computeCaffeineAmount();
            FirebaseUIActivity.getUserHistoryFb();
            FirebaseUIActivity.getUsernameFb();
        } else {
            FirebaseUIActivity.attachListener();
        }
        FirebaseUIActivity.queryDatabaseForAllLocations();
    }

    public static Context getAppContext() {
        return MapsActivity.context;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(this, "onMapReady() called", Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        enableMyLocation();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        } catch (SecurityException se) {

        }
        LatLng USC = new LatLng(34.0202, -118.2858);
        userLocation = USC;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(USC, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
        mMap.setMinZoomPreference(15);

        if (FirebaseUIActivity.isUserLoggedIn()) {
            if (FirebaseUIActivity.getNewSignIn()) {
                FirebaseUIActivity.queryDatabaseForCurrentUserLocations();
                FirebaseUIActivity.addUserToFirestore();
                FirebaseUIActivity.setNewSignIn(false);
                FirebaseUIActivity.checkAdmin(this);
            }
            displayLocations();
        }

//      Creating listeners for markers on the map to show their data
//      mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
              @Override
              public boolean onMarkerClick(Marker marker) {
                  BottomPanel bottomSheet = null;
                  if (markerToLocId.containsKey(marker)) {
                      bottomSheet = new BottomPanel(markerToName.get(marker), markerToLocId.get(marker), markerToCoord.get(marker), userLocation);
                  } else {
                      bottomSheet = new BottomPanel("Ooops, sorry!", "", null, null);
                  }
                  bottomSheet.show(getSupportFragmentManager(), marker.getTitle());
                  bottomSheet.self = bottomSheet;
                  return false;
              }
          }
        );
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Intent intent3 = new Intent(getBaseContext(), ProfileActivity.class);
        startActivity(intent3);
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUIActivity.detachListener();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        if (addLocationButton)
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

            case R.id.profileBtn:
                Intent intent2 = new Intent(this, ProfileActivity.class);
                startActivity(intent2);
                return true;

            case R.id.View_History:
                //Toast.makeText(MapsActivity.this, "View_History", Toast.LENGTH_LONG).show();
                Intent historyIntent = new Intent(this, UserHistoryActivity.class);
                startActivity(historyIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideAddLocationButton() {
        addLocationButton = false;
    }

    public void showAddLocationButton() {
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
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLocation, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }


    @Override
    public void onStatusChanged (String provider,int status, Bundle extras){

    }

    @Override
    public void onProviderEnabled (String provider){

    }

    @Override
    public void onProviderDisabled (String provider){

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
                                boolean verified = document.getBoolean("Verified");
                                if (verified) {
                                    String location_name = document.getString("Name");
                                    GeoPoint coordinates = document.getGeoPoint("Coordinates");
                                    LatLng latLng = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());

//                                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(location_name));
                                    Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected")
                                            .icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(R.drawable.coffee, location_name))));
                                    locIdToMarker.put(document.getId(), newMarker);
                                    markerToLocId.put(newMarker, document.getId());
                                    markerToName.put(newMarker, location_name);
                                    markerToCoord.put(newMarker, coordinates);
                                }
                            }
                        } else {
                            Log.d("Some string", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void showMissingPermissionError() {
        PermissionsUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    private Bitmap writeTextOnDrawable(int drawableId, String text) {


        Bitmap b = BitmapFactory.decodeResource(getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);
        Bitmap bm = Bitmap.createScaledBitmap(b, 150, 150, false);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(getBaseContext(), 13));


        Rect textRect = new Rect();
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.WHITE);

        paint.getTextBounds(text, 0, text.length(), textRect);
        int text_width = textRect.width();

//        textRect.offsetTo(0, 30);
        Bitmap bitmap = Bitmap.createBitmap(text_width, 220, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, text_width, 65, rectPaint);
        canvas.drawBitmap(bm, (text_width / 2) - 70, 65, paint);


        canvas.drawText(text, (text_width / 2), 50, paint);

        return bitmap;
    }


    public static int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);

    }
}














