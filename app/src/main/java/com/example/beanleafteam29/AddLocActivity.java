package com.example.beanleafteam29;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddLocActivity extends AppCompatActivity {
    private EditText Name;
    private EditText Address;
    private Button Add;

    private static final String TAG = "AddLocActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loc);

        Name = findViewById(R.id.input_name);
        Address = findViewById(R.id.input_address);
        Add = findViewById(R.id.btn_add);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLocation();
            }
        });

    }

    public void addLocation() {
        Log.d(TAG, "AddLoc");

        if (!validate()) {
            AddFailed();
            return;
        }

        Add.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AddLocActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Location...");
        progressDialog.show();

        String name = Name.getText().toString();
        String address = Address.getText().toString();

        FirebaseUIActivity.addLocation(name, address);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either AddSuccess or AddFail
                        // depending on success
                        AddSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);

        FirebaseUIActivity.queryDatabaseForCurrentUserLocations();
    }

    public void AddSuccess() {
        Add.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void AddFailed() {
        Toast.makeText(getBaseContext(), "Add Location Failed", Toast.LENGTH_LONG).show();
        Add.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String loc_name = Name.getText().toString();
        String address = Address.getText().toString();

        if (loc_name.isEmpty()) {
            Name.setError("Enter Valid Store Name");
            valid = false;
        } else {
            Name.setError(null);
        }

        if (address.isEmpty()) {
            Address.setError("Enter Valid Address");
            valid = false;
        } else {
            Address.setError(null);
        }


        return valid;
    }
}
