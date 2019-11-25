package com.example.beanleafteam29;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import android.app.Dialog;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.VisibleForTesting;

public class Edit_Location extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private EditText item;
    private EditText caff;
    private EditText price;
    private Button confirm;
    private ImageButton delete;
    private static final String TAG = "Edit_Location";
    Dialog myDialog; //used for pop-ups
    private String myLocation = new String();
    private static List<Map<String, Object> > input = new ArrayList<>();
    private static List<Map<String, Object> > deleteTracker = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__location);
        myDialog = new Dialog(this);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        myLocation = intent.getStringExtra("locationID");

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        //recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        getLocMenu(myLocation);
        mAdapter = new MyAdapter(input);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

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

        if (!validate()) {
            AddFailed();
            return;
        }

        confirm.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Edit_Location.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Adding Menu Item...");
        progressDialog.show();

        String item_name = item.getText().toString();
        Double item_caff = new Double(caff.getText().toString());
        Double item_price = new Double(price.getText().toString());

        Map<String, Object> m = new HashMap<>();
        m.put("Caffeine", item_caff);
        m.put("Name", item_name);
        m.put("Price", item_price);
        FirebaseUIActivity.addElementToMenu(m, myLocation); //update first
        mAdapter.add(mAdapter.getItemCount(), m);
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

    public void deleteMenu(View v){
        deleteTracker = new ArrayList<>();
        deleteTracker.addAll(mAdapter.delete_list()); //mAdapter.delete_list -- should update UI via adapter class
        if(deleteTracker == null){
            Toast.makeText(getBaseContext(), "Delete Menu Item Failed", Toast.LENGTH_LONG).show();
        }
        for (Map<String, Object> map : deleteTracker) {

            FirebaseUIActivity.deleteElementFromMenu(myLocation, map.get("Name").toString());

        }
        input = new ArrayList<>(mAdapter.updated_list());

    }

    public void AddSuccess() {
        confirm.setEnabled(true);
        setResult(RESULT_OK, null);
        myDialog.dismiss();
    }

    public void AddFailed() {
        Toast.makeText(getBaseContext(), "Add Menu Item Failed", Toast.LENGTH_LONG).show();
        confirm.setEnabled(true);
    }

    public boolean validate() {
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

    //used to intailize the menu items
    public static void getLocMenu(String myLocation) {
        int counter = 0;
        if (FirebaseUIActivity.isUserLoggedIn()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Locations/" + myLocation + "/Menu")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() != 0) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> myData = document.getData();
                                        input.add(myData);
                                        System.out.println(document);
                                        System.out.println(task.getResult().size());
                                    }

                                }
                            } else {
                                Log.d("getLocMenu", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

    /**
     * Only called from test
     */
//    @VisibleForTesting
//    @NonNull
//    public IdlingResource getIdlingResource() {
//        if (mIdlingResource == null) {
//            mIdlingResource = new SimpleIdlingResource();
//        }
//        return mIdlingResource;
//    }

}


