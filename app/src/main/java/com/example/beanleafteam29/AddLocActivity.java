package com.example.beanleafteam29;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AddLocActivity extends AppCompatActivity {
    //Variables for the location
    private EditText Name;
    private EditText Address;
    private Button Add;

    //Menu Add variables
    private EditText item;
    private EditText caff;
    private EditText price;
    private Button confirm;
    private ImageButton delete;
    Dialog myDialog; //used for pop-ups
    private static final String TAG = "AddLocActivity";
    private List<Map<String, Object>> totalMenu; //used to add all the menu items to database
    private List<Map<String, Object>> deleteTracker;
    //For Recycler View
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

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

        //Menu Stuff
        totalMenu = new ArrayList<>();
        myDialog = new Dialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        List<Map<String, Object>> initial = new ArrayList<>();
//        mAdapter = new MyAdapter(initial); //-- don't need to show initial items - pass in empty
//        recyclerView.setAdapter(mAdapter);

    }


    public void addLocation() {
        Log.d(TAG, "AddLoc");

        if (!validate()) {
            AddFailed();
            return;
        }

        //Make sure store owner can't add without an initial Menu
        if(totalMenu == null || totalMenu.size() == 0){
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

        FirebaseUIActivity.addLocation(name, address, this);
        //for
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

    //Add Menu Stuff Starting Here
    public void ShowPopup(View v) { //used to show popup
        TextView txtclose;
        myDialog.setContentView(R.layout.popup_add);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        confirm = (Button) myDialog.findViewById(R.id.btn_menu);
        item = (EditText) myDialog.findViewById(R.id.input_item);
        price = (EditText) myDialog.findViewById(R.id.input_price);
        caff = (EditText) myDialog.findViewById(R.id.input_caff);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMenu();
            }
        });
        myDialog.show();
    }

    public void addMenu(){

        Log.d(TAG, "AddMenu");

        if (!validateM()) {
            AddFailedM();
            return;
        }

        confirm.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AddLocActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Menu Item...");
        progressDialog.show();

        String item_name = item.getText().toString();
        Double item_caff = new Double(caff.getText().toString());
        Double item_price = new Double(price.getText().toString());

//        //Adding Menu to Data Base
        Map<String, Object> m = new HashMap<>();
        m.put("Caffeine", item_caff);
        m.put("Name", item_name);
        m.put("Price", item_price);
        totalMenu.add(m); //update to send to firebase later
//        FirebaseUIActivity.addElementToMenu(m, myLocation); //update first
        mAdapter = new MyAdapter(totalMenu); //-- don't need to show initial items - pass in empty
        recyclerView.setAdapter(mAdapter);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either AddSuccess or AddFail
                        // depending on success
                        AddSuccessM();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);


    }

    //delete Menu Items
    public void deleteMenu(View v){

        List<Map<String, Object>> temp = new ArrayList<>();

        for(int i = 0; i < MyAdapter.itemStateArray.size(); i++) {
            if (!MyAdapter.itemStateArray.valueAt(i)) {
                int key = MyAdapter.itemStateArray.keyAt(i);
                Map<String, Object> m = totalMenu.get(key);
                temp.add(m);
            }
        }

        mAdapter = new MyAdapter(temp);
        recyclerView.setAdapter(mAdapter);
        totalMenu = temp;
    }

    public void AddSuccessM() {
        confirm.setEnabled(true);
        setResult(RESULT_OK, null);
        myDialog.dismiss();
    }

    public void AddFailedM() {
        Toast.makeText(getBaseContext(), "Add Menu Item Failed", Toast.LENGTH_LONG).show();
        confirm.setEnabled(true);
    }

    public boolean validateM() {
        boolean valid = true;
        boolean d1 = true;
        boolean d2 = true;

        String item_name = item.getText().toString();
        String item_caff = caff.getText().toString();
        String item_price = price.getText().toString();

        //check for number inputs
        try {
            double d = Double.parseDouble(item_caff);
        } catch (NumberFormatException | NullPointerException nfe) {
            d1 =  false;
        }

        //check for number inputs
        try {
            double d = Double.parseDouble(item_price);
        } catch (NumberFormatException | NullPointerException nfe) {
            d2 =  false;
        }

        if (item_name.isEmpty()) {
            item.setError("Enter Valid Item Name");
            valid = false;
        } else {
            item.setError(null);
        }

        if (item_caff.isEmpty()) {
            caff.setError("Enter Valid Caffeine Amount");
            valid = false;
        } else if(!d1){
            caff.setError("Input is not numerical");
            valid = false;
        }
        else {
            caff.setError(null);
        }

        if (item_price.isEmpty()) {
            price.setError("Enter Valid Price Amount");
            valid = false;
        }else if(!d2){
            price.setError("Input is not numerical");
            valid = false;
        }
        else {
            price.setError(null);
        }


        return valid;
    }
}
