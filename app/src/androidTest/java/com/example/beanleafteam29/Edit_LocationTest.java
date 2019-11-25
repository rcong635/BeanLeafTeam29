package com.example.beanleafteam29;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
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
import androidx.test.InstrumentationRegistry;
import android.content.Intent;

import androidx.test.espresso.IdlingResource;
import static org.hamcrest.Matcher.*;
@RunWith(AndroidJUnit4.class)
public class Edit_LocationTest {

    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<Edit_Location> mActivityTestRule = new ActivityTestRule<Edit_Location>(Edit_Location.class, false, false);
//    @Rule
//    public IntentsTestRule<Edit_Location> intentsTestRule = new IntentsTestRule<>(Edit_Location.class);

    @Before
    public void setUp() throws Exception {
        //different locations would have different locId
        Intent i = new Intent();
        i.putExtra("locationID", "7vufQOykpFmHKM0Itlm4");
        mActivityTestRule.launchActivity(i);
//        mActivityTestRule.onActivity(new mActivityTestRule.ActivityAction<Edit_Location>() {
//            @Override
//            public void perform(Edit_Location activity) {
//                mIdlingResource = activity.getIdlingResource();
//                // To prove that the test fails, omit this call:
//                IdlingRegistry.getInstance().register(mIdlingResource);
//            }
//        });
    }

    @Test
    public void initial () throws InterruptedException //data is loaded and pop works
    {

        Espresso.onView(withId(R.id.imageView2)).perform(click());
        onView(withId(R.id.btn_menu))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.input_item)).perform(click());
        closeSoftKeyboard();
        Thread.sleep(2000);
        Espresso.onView(withId(R.id.txtclose)).perform(click());

    }

    @Test
    public void testDelete() throws InterruptedException {
        Espresso.onView(withId(R.id.imageView2)).perform(click());
        onView(withId(R.id.btn_menu))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.input_item)).perform(click());
        closeSoftKeyboard();
        Espresso.onView(withId(R.id.txtclose)).perform(click());

//        onData(hasEntry(equalTo(ListViewSample.ROW_TEXT),is("List item: 25")))
//                .onChildView(withId(R.id.rowToggleButton)).perform(click());
//        Thread.sleep(1000);
//        onData(hasEntry(equalTo(ListViewSample.ROW_TEXT),is("List item: 25")))
//                .onChildView(withId(R.id.rowToggleButton)).check(matches(isChecked()));
    }

    @Test
    public void testAdd() throws InterruptedException
    {
        Espresso.onView(withId(R.id.imageView2)).perform(click());
        onView(withId(R.id.btn_menu))
                .inRoot(isDialog()) // <---
                .check(matches(isDisplayed()));
        Espresso.onView(withId(R.id.input_item)).perform(typeText("Red Bull"));
        Espresso.onView(withId(R.id.input_price)).perform(typeText("3.25"));
        Espresso.onView(withId(R.id.input_caff)).perform(typeText("120"));
        Espresso.onView(withId(R.id.btn_menu)).perform(click());

        onView(withId(R.id.btn_menu)).inRoot(isDialog()).check(doesNotExist());
        Thread.sleep(3000);
    }

    @After
    public void tearDown() throws Exception {
    }
}