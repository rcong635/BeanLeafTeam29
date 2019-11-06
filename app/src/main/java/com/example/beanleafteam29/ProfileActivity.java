package com.example.beanleafteam29;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.getUid;
import static com.example.beanleafteam29.FirebaseUIActivity.getUserLocations;

public class ProfileActivity extends AppCompatActivity {
    private TextView question;
    private TextView header;

    private HashMap<String, QueryDocumentSnapshot> locations;
    private HashMap<String, String> myLocations;

    private TextView caffeineCaption;
    private TextView caffeineAmount;
    private TextView yourLocationsTV;
    private Button viewHistBtn;
    private RadioGroup rg;
    private ScrollView scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        header = findViewById(R.id.profileHeader);
        question = findViewById(R.id.profileQuestion);
        caffeineCaption = findViewById(R.id.caffeineAmount);
        caffeineAmount = findViewById(R.id.profileProgressBar);
        viewHistBtn = findViewById(R.id.profileViewHistBtn);
        rg = findViewById(R.id.profileRadioGroup);
        yourLocationsTV = findViewById(R.id.profileYourLocationsTV);
        scroller = findViewById(R.id.profileScroller);
        header.setText("Hello " + FirebaseUIActivity.getUserName());




        //set the name
        caffeineAmount.setText("" + FirebaseUIActivity.getCaffeineAmount());


        // if user is and admin change the question otherwise hide the non admin stuff
        if (FirebaseUIActivity.getIsAdmin()){
            question.setText("You are a Merchant! :)");
            rg.setVisibility(View.GONE);
        }
        else {
            scroller.setVisibility(View.GONE);
            yourLocationsTV.setVisibility(View.GONE);
        }



        viewHistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(getBaseContext(), UserHistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.radioBtnYes:
                        FirebaseUIActivity.makeAdmin();
//                      Toast.makeText(getBaseContext(), "Yes Clicked", Toast.LENGTH_SHORT).show();
                        question.setText("You are a Merchant! :)");
                        rg.setVisibility(View.GONE);
                        scroller.setVisibility(View.VISIBLE);
                        yourLocationsTV.setVisibility(View.VISIBLE);
                        break;
                    case R.id.radioBtnNo:
//                        Toast.makeText(getBaseContext(), "No Clicked", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

//        val name = findViewById< EditText >(R.id.profile);
//        val header = findViewById<TextView>(R.id.profileHeader);

        myLocations = new HashMap<>();
        locations = getUserLocations();
        Iterator locationsIterator = locations.entrySet().iterator();
        while (locationsIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)locationsIterator.next();
            LayoutInflater inflater = getLayoutInflater();
            View locationView = inflater.inflate(R.layout.editable_location, null);
            ViewGroup locationsView = findViewById(R.id.locations);
            String id = (String) mapElement.getKey();
            QueryDocumentSnapshot value = (QueryDocumentSnapshot) mapElement.getValue();
            String name = value.getString("Name");
            myLocations.put(id, name);
            TextView nameView = locationView.findViewById(R.id.locationName);
            nameView.setText(name);
            locationsView.addView(locationView, 0);
        }
    }

    public void OnEditButtonClicked(View v) {
        ConstraintLayout editLayout = (ConstraintLayout) v.getParent();

        TextView location = (TextView) editLayout.getChildAt(0);
        String locationName = location.getText().toString();

        Iterator locationsIterator = locations.entrySet().iterator();
        String locationID = "";
        while (locationsIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)locationsIterator.next();
            QueryDocumentSnapshot value = (QueryDocumentSnapshot) mapElement.getValue();
            String name = value.getString("Name");
            if (name.equals(locationName)) {
                locationID = (String) mapElement.getKey();
            }
        }

        Intent editLocationIntent = new Intent(this, Edit_Location.class);
        editLocationIntent.putExtra("locationID", locationID);
        startActivity(editLocationIntent);
    }
}
