package com.example.beanleafteam29;

import android.os.Bundle;

import android.view.View;

import android.view.LayoutInflater;

import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    private Button viewHistBtn;
    private RadioGroup rg;



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

        header.setText("Hello " + FirebaseUIActivity.getUserName());

        if (FirebaseUIActivity.getIsAdmin()){
            question.setText("You are a Merchant! :)");
            rg.setVisibility(View.GONE);
        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.radioBtnYes:

                        Toast.makeText(getBaseContext(), "Yes Clicked", Toast.LENGTH_SHORT).show();
                        question.setText("You are a Merchant! :)");
                        rg.setVisibility(View.GONE);
                        break;
                    case R.id.radioBtnNo:
                        Toast.makeText(getBaseContext(), "No Clicked", Toast.LENGTH_SHORT).show();
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

    }
}
