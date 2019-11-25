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
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
@RunWith(AndroidJUnit4.class)
public class AddLocActivityTest {

    @Rule
    public ActivityTestRule<AddLocActivity> mActivityTestRule = new ActivityTestRule<AddLocActivity>(AddLocActivity.class);
    private String name = "BBCM Cafe";
    private String address = "3301 S Hoover St, Los Angeles, CA 90007";
    private String user = "qeKjeRhtpZNQZUAN5Y6wE8qjDBo1";

    @Before
    public void setUp() throws Exception {
    }

    @Test //This one exams the general input of Add Location Activity
    public void testUserInput()
    {
        //Make sure we cannot add without necessary fields
        Espresso.onView(withId(R.id.input_name)).perform(typeText(name));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.btn_add)).perform(click());

        //We still cannot add without the initial menu
        Espresso.onView(withId(R.id.input_address)).perform(typeText(address));
        Espresso.closeSoftKeyboard();
        Espresso.onView(withId(R.id.btn_add)).perform(click());
        //check the Text -- maybe toast -- give assertion that add Location doesn't work
        //onView(withText(R.string.TOAST_STRING)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
    }

    @Test //This one tests the recycleview
    public void menuInput() throws InterruptedException {
        //Opens the popup
        Espresso.onView(withId(R.id.imageView2)).perform(click());
        onView(withId(R.id.btn_menu))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));

        //Close the popup -- prematurely
        Espresso.onView(withId(R.id.txtclose)).perform(click());
        onView(withText("ADD MENU ITEM")).check(doesNotExist());
        //reopen with inputs this time
        Espresso.onView(withId(R.id.imageView2)).perform(click());

        //give some inputs
        Espresso.onView(withId(R.id.input_item)).perform(typeText("Black Coffee"));
        Espresso.onView(withId(R.id.input_price)).perform(typeText("2.25"));
        Espresso.onView(withId(R.id.input_caff)).perform(typeText("75"));
        Espresso.onView(withId(R.id.btn_menu)).perform(click());

        onView(withId(R.id.btn_menu)).inRoot(isDialog()).check(doesNotExist());
        Thread.sleep(3000);
        Espresso.onView(withId(R.id.btn_add)).perform(click());
    }

    @Test //Successful Input of a new location with menu item -- goes back to map to check menu
    public void successful() throws InterruptedException
    {
        //Make sure we cannot add without necessary fields
        Espresso.onView(withId(R.id.input_name)).perform(typeText(name));
        Espresso.onView(withId(R.id.input_address)).perform(typeText(address));
        Espresso.closeSoftKeyboard();


        Espresso.onView(withId(R.id.imageView2)).perform(click());
        onView(withId(R.id.btn_menu))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));

        //give some inputs
        Espresso.onView(withId(R.id.input_item)).perform(typeText("Color Burst Latte"));
        Espresso.onView(withId(R.id.input_price)).perform(typeText("7.00"));
        Espresso.onView(withId(R.id.input_caff)).perform(typeText("125"));
        Espresso.onView(withId(R.id.btn_menu)).perform(click());

        onView(withId(R.id.btn_menu)).inRoot(isDialog()).check(doesNotExist());
        Thread.sleep(3000);
        Espresso.onView(withId(R.id.btn_add)).perform(click());
        //should add to our database

    }

    @After
    public void tearDown() throws Exception {
    }
}