package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
        TextView item1PriceView = findViewById(R.id.Item1Price);
        item1PriceView.setText(item1Price);

        String item1Caffeine = "165 mg";
        TextView item1CaffeineView = findViewById(R.id.Item1Caffeine);
        item1CaffeineView.setText(item1Caffeine);

        //FirebaseUIActivity.get

    }

}
