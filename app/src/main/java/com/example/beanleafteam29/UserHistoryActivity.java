package com.example.beanleafteam29;

import android.os.Bundle;
import android.util.Log;
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
import com.google.type.Date;

import java.text.SimpleDateFormat;
import java.util.Map;

import static com.example.beanleafteam29.FirebaseUIActivity.mFirebaseAuth;

public class UserHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "onCreate() called", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        String item1Name = "Cappuccino";
        TextView item1NameView = findViewById(R.id.Item1Name);
        item1NameView.setText(item1Name);

        String item1Time = "November 2, 2019  11:18 AM";
        TextView item1TimeView = findViewById(R.id.Item1Time);
        item1TimeView.setText(item1Time);

        String item1Location = "Coffee Bean and Tea Leaf";
        TextView item1LocationView = findViewById(R.id.Item1Location);
        item1LocationView.setText(item1Location);

        String item1Price = "$9.99";
        final TextView item1PriceView = findViewById(R.id.Item1Price);
        item1PriceView.setText(item1Price);

        String item1Caffeine = "165 mg";
        TextView item1CaffeineView = findViewById(R.id.Item1Caffeine);
        item1CaffeineView.setText(item1Caffeine);

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


                                    int size = task.getResult().size();


                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> myData = document.getData();
                                        System.out.println(document);

                                        String name = (String) myData.get("Name");
                                        TextView nameView = findViewById(R.id.Item1Name);
                                        nameView.setText(name);

//                                        Timestamp timeStamp = (Timestamp) myData.get("Date");
//                                        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                                        sfd.format(new Date(timeStamp));
//                                        TextView timeView = findViewById(R.id.Item1Time);
//                                        String timeString = time.toString();
//                                        timeView.setText(timeString);

                                        String location = (String) myData.get("LocationName");
                                        TextView locationView = findViewById(R.id.Item1Location);
                                        locationView.setText(location);

                                        Double price = (Double) myData.get("Price");
                                        TextView priceView = findViewById(R.id.Item1Price);
                                        String priceString = price.toString();
                                        priceView.setText(priceString);

                                        Long caffeine = (Long) myData.get("Caffeine");
                                        TextView caffeineView = findViewById(R.id.Item1Caffeine);
                                        String caffeineString = caffeine.toString();
                                        caffeineView.setText(caffeineString);

                                        System.out.println(task.getResult().size());
                                    }
                                }
                            } else {
                                Log.d("getUserHistory", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

    }

    public static void getUserHistory() {

    }

}
