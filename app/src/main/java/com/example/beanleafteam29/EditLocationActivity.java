package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditLocationActivity extends AppCompatActivity {
    private EditText NameET;
    private EditText AddressET;
    private TextView NameTV;
    private TextView AddressTV;
    private TextView MenuTV;
    private Button SaveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        NameET = findViewById(R.id.editLocName);
        AddressET = findViewById(R.id.editLocAddress);
        NameTV = findViewById(R.id.editLocNameTV);
        AddressTV = findViewById(R.id.editLocAddressTV);
        MenuTV = findViewById(R.id.editLocMenuTV);
        SaveBtn = findViewById(R.id.editLocSaveBtn);
    }
}
