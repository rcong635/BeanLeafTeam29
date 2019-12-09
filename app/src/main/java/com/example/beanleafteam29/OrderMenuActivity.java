package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.HashMap;
import static com.example.beanleafteam29.FirebaseUIActivity.getCaffeineAmount;
import static com.example.beanleafteam29.FirebaseUIActivity.getUid;

public class OrderMenuActivity extends AppCompatActivity {

    ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    String locationName;
    String locationID;

    static double mUserLat;
    static double mUserLng;
    static double mLocationLat;
    static double mLocationLng;
    double distanceThreshold = 200.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_menu);
        mUserLat = getIntent().getDoubleExtra("userLat", 0);
        mUserLng = getIntent().getDoubleExtra("userLng", 0);
        mLocationLat = getIntent().getDoubleExtra("locationLat", 0);
        mLocationLng = getIntent().getDoubleExtra("locationLng", 0);

        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            locationID = getIntent().getStringExtra("locationID");
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
                                        //System.out.println(document);
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
                                        menuView.addView(itemView,0);
                                        checkBoxes.add((CheckBox) itemView.findViewById(R.id.checkbox));
                                    }
                                } else {
                                    TextView noItems = findViewById(R.id.noItems);
                                    noItems.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.d("OrderMenuActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    public void OnBuyButtonClicked(View v) {
        HashMap<String, Object> order = new HashMap<>();
        HashMap<String, Object> locOrder = new HashMap<>();
        final ArrayList<HashMap<String, Object>> orders = new ArrayList<>();
        final ArrayList<HashMap<String, Object>> locOrders = new ArrayList<>();
        long caffeine = 0;
        final long caffeineFinal; // variable used in inner class, it's just a copy of caffeineInOrder
        if (checkDistance() < distanceThreshold) {
            long caffeineInOrder = 0;
            long caffeineConsumed = getCaffeineAmount();

            for (int i = 0; i < checkBoxes.size(); i++) {
                boolean checked = checkBoxes.get(i).isChecked();
                if (checked) {
                    order.clear();
                    locOrder.clear();

                    RelativeLayout itemLayout = (RelativeLayout) checkBoxes.get(i).getParent();
                    order.put("Name", ((TextView) itemLayout.getChildAt(1)).getText());
                    order.put("LocationName", locationName);
                    String priceString = (String) ((TextView) itemLayout.getChildAt(2)).getText();
                    priceString = priceString.substring(1);
                    order.put("Price", Double.valueOf(priceString));
                    String caffeineString = (String) ((TextView) itemLayout.getChildAt(3)).getText();
                    caffeine = caffeineToLong(caffeineString);
                    order.put("Caffeine", caffeine);
                    caffeineInOrder += caffeine;
                    order.put("Date", Timestamp.now());
                    order.put("LocationID", locationID);

                    //Location history
                    locOrder.put("Name", ((TextView) itemLayout.getChildAt(1)).getText());
                    locOrder.put("Customer", FirebaseUIActivity.getUserName());
                    locOrder.put("CustomerID", getUid());
                    locOrder.put("Price", Double.valueOf(priceString));
                    locOrder.put("Date", Timestamp.now());

                    orders.add((HashMap<String, Object>) order.clone());
                    locOrders.add((HashMap<String, Object>)locOrder.clone());
                }
            }
            caffeineFinal = caffeineInOrder;

            if (order.isEmpty()) {
                Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            }
            else if (caffeineInOrder + caffeineConsumed > 400) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Caffeine Warning");
                alertDialogBuilder
                        .setMessage("After this purchase, you will have exceeded the recommended daily caffeine limit (400mg). Are you sure you want to continue?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                addElementsToDatabase(orders, locOrders, caffeineFinal);
                                Toast.makeText(getBaseContext(), "Purchase completed", Toast.LENGTH_SHORT).show();
                                OrderMenuActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else {
                addElementsToDatabase(orders, locOrders, caffeineFinal);
                Toast.makeText(this, "Purchase completed", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Too Far!!!");
            alertDialogBuilder
                    .setMessage("You are too far to make an order. Try again when you are close to this place.")
                    .setCancelable(true)
                    .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private void addElementsToDatabase(ArrayList<HashMap<String, Object>> orders, ArrayList<HashMap<String, Object>> locOrders, long caffeineInOrder) {
        for(HashMap<String, Object> locOrder : locOrders) {
            FirebaseUIActivity.addElementToLocationHistory(locOrder, locationID);
        }

        for(HashMap<String, Object> order : orders) {
            FirebaseUIActivity.addElementToUserHistory(order); // THIS NEEDS TO BE CHANGED, PASS DOWN STRING THAT REPRESENTS THE ID

        }
        FirebaseUIActivity.setCaffeineLocal(FirebaseUIActivity.getCaffeineAmount() + caffeineInOrder);
    }

    public static long caffeineToLong(String caffeineString) {
        for (int i = 0; i < caffeineString.length(); i++) {
            if (!Character.isDigit(caffeineString.charAt(i))) {
                caffeineString = caffeineString.substring(0, i);
                return Long.valueOf(caffeineString);
            }
        }
        return -1;
    }

    public static double checkDistance() {
        double earthRadius = 6371 * 1000;
        double latDiff = Math.toRadians(getUserLat()-getLocationLat());
        double lngDiff = Math.toRadians(getUserLng()-getLocationLng());
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(getLocationLat())) * Math.cos(Math.toRadians(getUserLat())) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        return distance;
    }

    public static void setCoordinates(double userLat, double userLng, double locationLat, double locationLng) {
        mUserLat = userLat;
        mUserLng = userLng;
        mLocationLat = locationLat;
        mLocationLng = locationLng;
    }

    public static double getUserLat() {
        return mUserLat;
    }

    public static double getUserLng() {
        return mUserLng;
    }

    public static double getLocationLat() {
        return mLocationLat;
    }

    public static double getLocationLng() {
        return mLocationLng;
    }

}
