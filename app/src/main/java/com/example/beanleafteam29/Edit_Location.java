package com.example.beanleafteam29;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Edit_Location extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__location);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to
        // improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) { //preallocate with data we already have
            input.add("Test" + i);
        }// define an adapter
        mAdapter = new MyAdapter(input);
        recyclerView.setAdapter(mAdapter);
    }

    public void addMenu(){
        Map<String, Object> m = new HashMap<>();
        m.put("Caffeine", 100);
        m.put("Name", "Some Weird Coffee");
        m.put("Price", 4.99);
        //FirebaseUIActivity.addElementToMenu(m, UID); //update first

        //MyAdapter.add();
    }

    public void deleteMenu(){

    }

    //Name of item -- string
    //Price -- double
    //Caffeine -- double

    //Map ^ hashmap holds all of that
    //Implement with bottom panel
    //location id -- intent extra -- pass the location ID
    //Also tell name of item u wanna delete --> for the data base

    //delete -- name only
    //add -- map

    //example
    //Map<String, Object> m = new Hashmap<>();
    //m.put("Caffeine", 100);
    //m.put("Name", "Some Weird Coffee");
    //m.put("Price", 4.99);
    //FirebaseUIActivity.addElementToMenu(m, UID);
    //FirebaseUIActivity.deleteElementFromMenu(UID, "Some Weird Coffee");

}


