package com.example.beanleafteam29;

import android.os.Bundle;
import android.view.View;
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


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.radioBtnYes:

                        Toast.makeText(getBaseContext(), "Yes Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioBtnNo:
                        Toast.makeText(getBaseContext(), "No Clicked", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });

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
