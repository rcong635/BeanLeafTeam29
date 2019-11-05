package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.mFirebaseAuth;

public class OrderMenuActivity extends AppCompatActivity {

    ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    String locationName;

    double userLat;
    double userLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_menu);
        userLat = getIntent().getDoubleExtra("userLat", 0);
        userLng = getIntent().getDoubleExtra("userLng", 0);

        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String locationID = getIntent().getStringExtra("locationID");
            locationName = getIntent().getStringExtra("locationName");
            db.collection("Locations/" + locationID + "/Menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() != 0) {
                                    TextView noItems = findViewById(R.id.noItems);
                                    noItems.setVisibility(View.INVISIBLE);
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        System.out.println(document);

                                        LayoutInflater inflater = getLayoutInflater();
                                        View itemView = inflater.inflate(R.layout.order_menu_item, null);
                                        ViewGroup menuView = findViewById(R.id.Menu);

                                        String name = document.getString("Name");
                                        TextView nameView = itemView.findViewById(R.id.ItemName);
                                        nameView.setText(name);

                                        double price = document.getDouble("Price");
                                        TextView priceView = itemView.findViewById(R.id.ItemPrice);
                                        String priceString = "$" + String.format("%.2f", price);
                                        priceView.setText(priceString);

                                        long caffeine = document.getLong("Caffeine");
                                        TextView caffeineView = itemView.findViewById(R.id.ItemCaffeine);
                                        String caffeineString = caffeine + " mg caffeine";
                                        caffeineView.setText(caffeineString);

                                        menuView.addView(itemView, 0);

                                        checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox));

                                        //System.out.println(task.getResult().size());
                                    }
                                } else {
                                    TextView noItems = findViewById(R.id.noItems);
                                    noItems.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.d("getUserHistory", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void OnBuyButtonClicked(View v) {
        if (checkDistance() < 50) {
            for (int i = 0; i < checkBoxes.size(); i++) {
                boolean checked = checkBoxes.get(i).isChecked();
                if (checked) {
                    Map<String, Object> order = new HashMap<>();
                    RelativeLayout itemLayout = (RelativeLayout) checkBoxes.get(i).getParent();
                    order.put("Name", ((TextView) itemLayout.getChildAt(1)).getText());
                    order.put("LocationName", locationName);
                    String priceString = (String) ((TextView) itemLayout.getChildAt(2)).getText();
                    priceString = priceString.substring(1);
                    order.put("Price", Double.valueOf(priceString));
                    String caffeineString = (String) ((TextView) itemLayout.getChildAt(3)).getText();
                    order.put("Caffeine", caffeineToLong(caffeineString));
                    order.put("Date", Timestamp.now());
                    FirebaseUIActivity.addElementToUserHistory(order);
                }
            }
            finish();
        }
    }

    public long caffeineToLong(String caffeineString) {
        for (int i = 0; i < caffeineString.length(); i++) {
            if (!Character.isDigit(caffeineString.charAt(i))) {
                caffeineString = caffeineString.substring(0, i);
                return Long.valueOf(caffeineString);
            }
        }
        return -1;
    }

    public float checkDistance(String locId) {
        LatLng location = getLatLng(locId);
        double lat_a = location.latitude;
        double lng_a = location.longitude;
        double lat_b = userLat;
        double lng_b = userLng;
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

}
