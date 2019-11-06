package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {
    private TextView question;
    private TextView header;
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

        header.setText("Hello " + FirebaseUIActivity.getUserName());
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
    }
}
