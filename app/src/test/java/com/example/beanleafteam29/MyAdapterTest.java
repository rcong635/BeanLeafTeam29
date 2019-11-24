package com.example.beanleafteam29;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class MyAdapterTest {
    private MyAdapter tester;

    @Before
    public void setUp() throws Exception {
        tester = new MyAdapter();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void delete_list() {
    }

    @Test
    public void add() {
        //make sure our sparse boolean array intialize them to false each
        fill_val();
        for(int i=0; i<5; i++){
           assertEquals("Sparse at: " + i , false, tester.check_state(i));
        }
        //check the value within the ListArray
        List<Map<String, Object>> p = new ArrayList<>(tester.updated_list());
        int i =0;
        for (Map<String, Object> map : p) {
            assertEquals("Name: " + i, "Testing "+ i, map.get("Name"));
            assertEquals("Caffiene: " + i, i, map.get("Caffeine"));
             assertEquals("Price: " + i, i, map.get("Price"));
             i++;
        }


    }

    @Test
    public void remove() { //tests for correctly re-indexing
    }

    @Test
    public void onCreateViewHolder() {
    }

    @Test
    public void onBindViewHolder() {
    }

    @Test
    public void getItemCount() {
        //tests for null
        assertEquals("Empty Vector: 0 ", 0, tester.getItemCount());


        //tests for 5, after add
        fill_val();
        assertEquals("Vector after adding 5 ", 5, tester.getItemCount());

        //tests for 2, after 3 removes
        tester.remove(tester.getItemCount() - 1);
        tester.remove(tester.getItemCount() - 1);
        tester.remove(tester.getItemCount() - 1);
        assertEquals("Vector after removing 3: 2 ", 2, tester.getItemCount());

    }

    private void fill_val() {
        for (int i = 0; i < 5; i++) {
            Map<String, Object> m = new HashMap<>();

            String name = "Testing " + i;
            m.put("Caffeine", i);
            m.put("Name", name);
            m.put("Price", i);
            tester.add(i, m);
        }
    }

    private void print_arr(List<Map<String, Object>> p) {
        int i = 0;
        for (Map<String, Object> map : p) {

                assertEquals("Name: " + i, "Testing "+ i, map.get("Name"));
                assertEquals("Caffiene: " + i, i, map.get("Caffeine"));
                assertEquals("Price: " + i, "Testing "+ i, map.get("Price"));
            i++;
        }
    }
}