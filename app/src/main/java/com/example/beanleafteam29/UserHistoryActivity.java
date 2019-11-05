package com.example.beanleafteam29;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.mFirebaseAuth;

public class UserHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        if(FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = mFirebaseAuth.getUid();
            db.collection("Users/" + uid + "/History")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size() != 0) {
                                    TextView noItems = findViewById(R.id.noItems);
                                    noItems.setVisibility(View.INVISIBLE);
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        System.out.println(document);

                                        LayoutInflater inflater = getLayoutInflater();
                                        View itemView = inflater.inflate(R.layout.user_history_item, null);
                                        ViewGroup historyView = findViewById(R.id.History);
                                        String name = document.getString("Name");
                                        TextView nameView = itemView.findViewById(R.id.ItemName);
                                        nameView.setText(name);

                                        Timestamp timeStamp = document.getTimestamp("Date");
                                        SimpleDateFormat sfd = new SimpleDateFormat("MM/dd/yyyy   HH:mm");
                                        TextView timeView = itemView.findViewById(R.id.ItemTime);
                                        String timeString = sfd.format(timeStamp.toDate());
                                        timeView.setText(timeString);


                                        String location = document.getString("LocationName");
                                        TextView locationView = itemView.findViewById(R.id.ItemLocation);
                                        locationView.setText(location);

                                        double price = document.getDouble("Price");
                                        TextView priceView = itemView.findViewById(R.id.ItemPrice);
                                        String priceString = "$" + price;
                                        priceView.setText(priceString);

                                        long caffeine = document.getLong("Caffeine");
                                        TextView caffeineView = itemView.findViewById(R.id.ItemCaffeine);
                                        String caffeineString = caffeine + "mg caffeine";
                                        caffeineView.setText(caffeineString);

                                        historyView.addView(itemView, 0);

                                        System.out.println(task.getResult().size());
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

}
