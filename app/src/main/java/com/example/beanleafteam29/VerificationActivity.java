package com.example.beanleafteam29;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class VerificationActivity extends AppCompatActivity {
    String locationID;
    String verificationCode = "team29";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        locationID = getIntent().getStringExtra("locationID");
//        String locationName = getIntent().getStringExtra("locationName");




    }


    public void OnFinalVerifyClicked(View v) {
        TextView password = findViewById(R.id.verificationCode);
        String mypassword = password.getText().toString();
        if ( mypassword.equals(verificationCode)){
            FirebaseUIActivity.setStoreVerified(locationID, true);

            Toast.makeText(this, "Verification completed", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            finish();

        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Wrong Verification Code!!!");
            alertDialogBuilder
                    .setMessage("Please try again. The code was not correct.")
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
}
