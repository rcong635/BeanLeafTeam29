package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.getUid;
import static com.example.beanleafteam29.FirebaseUIActivity.getUserLocations;

public class ProfileActivity extends AppCompatActivity {
    private TextView Name;
    private TextView header;
    private HashMap<String, QueryDocumentSnapshot> locations;
    private HashMap<String, String> myLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        header = findViewById(R.id.profileHeader);
        Name = findViewById(R.id.profileQuestion);
//        val name = findViewById< EditText >(R.id.profile);
//        val header = findViewById<TextView>(R.id.profileHeader);

        locations = getUserLocations();
        Iterator locationsIterator = locations.entrySet().iterator();
        while (locationsIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)locationsIterator.next();
            String owner = locations.get(mapElement.getKey()).getString("Owner");
            if (owner == getUid()) {
                myLocations.put((String) mapElement.getKey(), locations.get(mapElement.getKey()).getString("Name"));
            }
        }
    }

    public void OnEditButtonClicked(View v) {

    }
}
