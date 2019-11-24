package com.example.beanleafteam29;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
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
@RunWith(AndroidJUnit4.class)
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
        Espresso.onView(withId(R.id.input_address)).perform(typeText(address));
        //close soft keyboard
        Espresso.closeSoftKeyboard();
        //perform button click
        Espresso.onView(withId(R.id.btn_add)).perform(click());
        //check the Text -- maybe toast -- give assertion that add Location doesn't work

    }

    @Test //This one tests the recycleview
    public void menuInput(){
        //Opens the popup
        Espresso.onView(withId(R.id.imageView2)).perform(click());
        //Espresso.onView(withText("ADD MENU ITEM")).check(isDisplayed());
        //Close the popup -- prematurely
        Espresso.onView(withId(R.id.txtclose)).perform(click());
        onView(withText("ADD MENU ITEM")).check(doesNotExist());
        //reopen with inputs this time
        Espresso.onView(withId(R.id.imageView2)).perform(click());

        //give some inputs
        Espresso.onView(withId(R.id.input_item)).perform(typeText("Black Coffee"));
        Espresso.onView(withId(R.id.btn_menu)).perform(click());
        //give assertion that add menu fail

        //give rest of the inputs
        Espresso.onView(withId(R.id.input_price)).perform(typeText("2.25"));
        Espresso.onView(withId(R.id.input_caff)).perform(typeText("75"));

        //click add btn + give assertion success
        Espresso.onView(withId(R.id.btn_menu)).perform(click());

        //input some text in the edit text
        //Espresso.onView(withId(R.id.input_name)).perform(typeText(name));
        //Espresso.closeSoftKeyboard();
        //Espresso.onView(withId(R.id.btn_add)).perform(click());

        //back to recycle view
        //Espresso.onView(withId(R.id.my_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));


    }

    @Test //Successful Input of a new location with menu item -- goes back to map to check menu
    public void successful()
    {

    }

    @After
    public void tearDown() throws Exception {
    }
}