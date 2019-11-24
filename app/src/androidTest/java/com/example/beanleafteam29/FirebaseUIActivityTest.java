package com.example.beanleafteam29;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FirebaseUIActivityTest {

    private String locationId = null;

    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule(MapsActivity.class);

    @Test
    public void testComputeCaffeine() throws Exception {
        FirebaseUIActivity.computeCaffeineAmount();
        Thread.sleep(4000);
        long old_caffeine = FirebaseUIActivity.getCaffeineAmount();
        assertTrue(old_caffeine >= 0);
        Map<String, Object> order = new HashMap<>();
        order.put("Name", "Espresso");
        order.put("LocationName", "Starbucks");
        order.put("Price", 12.99);
        order.put("Caffeine", 120);
        order.put("Date", Timestamp.now());
        FirebaseUIActivity.addElementToUserHistory(order);
        Thread.sleep(4000);
        // bug found here - computeCaffeineAmount() return values higher than expected
        // every time computeCaffeineAmount() is called, today's caffeine gets added to the variable,
        // instead it should only return today's amount.
        FirebaseUIActivity.computeCaffeineAmount();
        Thread.sleep(4000);
        long new_caffeine = FirebaseUIActivity.getCaffeineAmount();
        assertEquals(new_caffeine, old_caffeine + 120);
    }

    // function that makes sure the location gets added and retrieved from Firebase correctly.
    @Test
    public void testAddLocation() throws Exception {
        FirebaseUIActivity.addLocation("Caffe Giallo", "1376 W 37th Pl, Los Angeles");
        Thread.sleep(4000);
        FirebaseUIActivity.queryDatabaseForCurrentUserLocations();
        Thread.sleep(4000);
        HashMap<String, QueryDocumentSnapshot> myLocations = FirebaseUIActivity.getUserLocations();
        boolean caffeGialloExists = false;

        for (Map.Entry mapElement : myLocations.entrySet()) {
            String key = (String) mapElement.getKey();
            QueryDocumentSnapshot value = (QueryDocumentSnapshot) mapElement.getValue();
            String name = value.getString("Name");
            String owner = value.getString("Owner");
            GeoPoint gp = value.getGeoPoint("Coordinates");
            assertNotNull(name);
            assertNotNull(owner);
            assertNotNull(gp);
            if(name.equals("Caffe Giallo"))
                caffeGialloExists = true;
        }
        assertTrue(caffeGialloExists);
    }

    @Test
    public void testMakeAdmin() throws Exception {

    }

    @Test
    public void testAddElementToMenu() throws Exception {
        // add location first
        testAddLocation();
        Map<String, Object> m = new HashMap<>();
        m.put("Caffeine", 200);
        m.put("Name", "Cappuccino con panna");
        m.put("Price", 14.99);

        // retrieve location id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Locations/")
                .whereEqualTo("Name", "Caffe Giallo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                locationId = document.getId();
                            }
                        } else {
                            Log.d("testAddElementToMenu", "Error getting documents: ", task.getException());
                        }
                    }
                });

        boolean cappuccinoExists = false;
        Thread.sleep(3000);
        assertNotNull(locationId);
        FirebaseUIActivity.addElementToMenu(m, locationId);
        Thread.sleep(3000);
        FirebaseUIActivity.getLocationMenuFb(locationId); // retrieves from database and place into variable
        Thread.sleep(3000);
        List<Map<String, Object> > menu = FirebaseUIActivity.getLocationMenu();
        assertTrue(menu.size() > 0);
        Map<String, Object> menuItem;
        for(int i = 0; i < menu.size(); i++) {
            menuItem = menu.get(i);
            if(menuItem.get("Name").equals("Cappuccino con panna"))
                cappuccinoExists = true;
        }
        assertTrue(cappuccinoExists);
    }

//    @Test
//    public void testDeleteElementFromMenu() throws Exception {
//        testAddElementToMenu();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Locations/")
//                .whereEqualTo("Name", "Caffe Giallo")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for(QueryDocumentSnapshot document : task.getResult()) {
//                                locationId = document.getId();
//                            }
//                        } else {
//                            Log.d("testDeleteElementFromMenu", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        Thread.sleep(3000);
//        FirebaseUIActivity.deleteElementFromMenu(locationId, "Cappuccino con panna");
//        Thread.sleep(3000);
//        FirebaseUIActivity.getLocationMenuFb(locationId); // retrieves from database and place into variable
//        Thread.sleep(3000);
//        List<Map<String, Object> > menu = FirebaseUIActivity.getLocationMenu();
//        assertTrue(menu.size() == 0);
//    }

    @Test
    public void testGetUid() {
        String uid = null;
        if(FirebaseUIActivity.isUserLoggedIn()) {
            uid = FirebaseUIActivity.getUid();
        }
        assertNotNull(uid);
        assertNotEquals("", uid);
    }
}