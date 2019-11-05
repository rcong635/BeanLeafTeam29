package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private TextView Name;
    private TextView header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        header = findViewById(R.id.profileHeader);
        Name = findViewById(R.id.profileQuestion);
//        val name = findViewById< EditText >(R.id.profile);
//        val header = findViewById<TextView>(R.id.profileHeader);
    }
}
