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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_menu);

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
                                        String priceString = "$" + price;
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

    public long caffeineToLong(String caffeineString) {
        for (int i = 0; i < caffeineString.length(); i++) {
            if (!Character.isDigit(caffeineString.charAt(i))) {
                caffeineString = caffeineString.substring(0, i);
                return Long.valueOf(caffeineString);
            }
        }
        return -1;
    }

}
