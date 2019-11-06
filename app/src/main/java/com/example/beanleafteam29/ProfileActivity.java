package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    private TextView question;
    private TextView header;
    private TextView caffeineLevel;
    private ProgressBar caffeineBar;
    private Button viewHistBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        header = findViewById(R.id.profileHeader);
        question = findViewById(R.id.profileQuestion);
        caffeineLevel = findViewById(R.id.caffeineAmount);
        caffeineBar = findViewById(R.id.profileProgressBar);
        viewHistBtn = findViewById(R.id.profileViewHistBtn);

//        val name = findViewById< EditText >(R.id.profile);
//        val header = findViewById<TextView>(R.id.profileHeader);
    }
}
