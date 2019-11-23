package com.example.beanleafteam29;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import static org.junit.Assert.*;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

public class AddLocActivityTest {

    @Rule
    public ActivityTestRule<AddLocActivity> mActivityTestRule = new ActivityTestRule<AddLocActivity>(AddLocActivity.class);
    private String name = "BBCM Cafe";
    private String address = "3301 S Hoover St, Los Angeles, CA 90007";

    @Before
    public void setUp() throws Exception {
    }

    @Test //This one exams the general input of Add Location Activity
    public void testUserInput()
    {
        //input some text in the edit text
        Espresso.onView(withId(R.id.input_name)).perform(typeText(name));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //perform button click
        Espresso.onView(withId(R.id.btn_add)).perform(click());
        //check the Text -- maybe toast

    }

    @Test //This one tests the recycleview
    public void menuInput(){

    }

    @Test //Successful Input of a new location with menu item -- goes back to map to check menu
    public void successful()
    {

    }

    @After
    public void tearDown() throws Exception {
    }
}