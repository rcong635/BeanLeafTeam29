package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.mFirebaseAuth;

public class SellerHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_history_2);

        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            String locationID = getIntent().getStringExtra("locationID");
            String locationName = getIntent().getStringExtra("locationName");
            TextView header = findViewById(R.id.SellerHeader);
            final TextView noItems = findViewById(R.id.SellerNoItems);

            final TextView mostPopularItem = findViewById(R.id.SellerPopularItem);
            final TextView popularItemPercentage = findViewById(R.id.SellerPercentage);
            final TextView totalSales = findViewById(R.id.SellerSalesNum);
            final TextView salesAmount = findViewById(R.id.SellerSalesAmount);
            final ScrollView scroller = findViewById(R.id.SellerScroller);




            final HashMap<String, Integer> itemCounts = new HashMap<>();

            header.setText(locationName + "'s History");

            String uid = mFirebaseAuth.getUid();
            db.collection("Locations/" + locationID + "/History")
                    .orderBy("Date")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() != 0) {


                                    int salesCounter = 0;
                                    double moneyCounter = 0.0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {

//                                        System.out.println(document);

                                        LayoutInflater inflater = getLayoutInflater();
                                        View itemView = inflater.inflate(R.layout.seller_history_item, null);
                                        ViewGroup historyView = findViewById(R.id.SellerHistory);
                                        String name = document.getString("Name");
                                        TextView nameView = itemView.findViewById(R.id.SellerName);
                                        nameView.setText(name);

                                        Timestamp timeStamp = document.getTimestamp("Date");
                                        SimpleDateFormat sfd = new SimpleDateFormat("MM/dd/yyyy @  HH:mm");
                                        TextView timeView = itemView.findViewById(R.id.SellerTime);
                                        String timeString = sfd.format(timeStamp.toDate());
                                        timeView.setText(timeString);


                                        String customerName = document.getString("Customer");
                                        TextView customerNameView = itemView.findViewById(R.id.SellerCustomer);
                                        customerNameView.setText("By: " + customerName);

                                        double price = document.getDouble("Price");
                                        TextView priceView = itemView.findViewById(R.id.SellerPrice);
                                        String priceString = "$" + String.format("%.2f", price);
                                        priceView.setText(priceString);

                                        historyView.addView(itemView, 0);

                                        salesCounter++;
                                        moneyCounter += price;
                                        itemCounts.put(name, itemCounts.getOrDefault(name,0) + 1);
                                    }

                                    int max = 0;
                                    String popularItem = new String();
                                    for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
                                        if (entry.getValue() > max) {
                                            max = entry.getValue();
                                            popularItem = entry.getKey();
                                        }
                                    }

                                    double percentage = (double)(100*max/((double)(salesCounter)));
                                    if (max > 1) {
                                        mostPopularItem.setText(popularItem);
                                        popularItemPercentage.setText("%" + String.format("%.2f", percentage));
                                    }
                                    else{
                                        mostPopularItem.setText(" ");
                                        popularItemPercentage.setText(" ");
                                    }

                                    totalSales.setText(String.valueOf(salesCounter));
                                    salesAmount.setText("$" + String.format("%.2f",moneyCounter));


                                } else {

                                    noItems.setText("No items purchased yet!");
                                    mostPopularItem.setText(" ");
                                    popularItemPercentage.setText(" ");
                                    totalSales.setText("0");
                                    salesAmount.setText("$0");
                                    scroller.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                Log.d("getUserHistory", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }
}
