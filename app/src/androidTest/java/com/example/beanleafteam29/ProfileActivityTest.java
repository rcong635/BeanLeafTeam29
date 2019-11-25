package com.example.beanleafteam29;
import android.view.View;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
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
import static org.hamcrest.CoreMatchers.not;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;




@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {
    public FirebaseUIActivity fb = new FirebaseUIActivity();

    @Rule
    public ActivityTestRule<MapsActivity> mMapActivityTestRule = new ActivityTestRule<>(MapsActivity.class);
    @Rule
    public ActivityTestRule<ProfileActivity> mActivityTestRule = new ActivityTestRule<>(ProfileActivity.class);
//    @Rule
//    public IntentsTestRule<UserHistoryActivity> intentsTestRule = new IntentsTestRule<>(UserHistoryActivity.class);

    private String merchantText = "You are a Merchant! :)";
    private String customerText = "Would you like to become a Merchant?";

    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @Test
    public void a0() throws InterruptedException {
        Thread.sleep(3000);

    }

    @Test
    public void test1_ChangingAccountType(){

        try {
            onView(withText(customerText)).check(matches(isDisplayed()));
            Espresso.onView(withId(R.id.radioBtnYes)).perform(click());
            Espresso.onView(withId(R.id.profileQuestion)).check(matches(withText(merchantText)));
            Espresso.onView(withId(R.id.radioBtnYes)).check(matches(not(isDisplayed())));
            Espresso.onView(withId(R.id.radioBtnNo)).check(matches(not(isDisplayed())));

            //view is displayed logic
        } catch (NoMatchingViewException e) {
            Espresso.onView(withId(R.id.profileQuestion)).check(matches(withText(merchantText)));
            Espresso.onView(withId(R.id.radioBtnYes)).check(matches(not(isDisplayed())));
            Espresso.onView(withId(R.id.radioBtnNo)).check(matches(not(isDisplayed())));

        }

    }

    @Test
    public void test2_ViewHistoryInProfile()
    {
        onView(withId(R.id.profileViewHistBtn)).perform(click());
        intended(hasComponent(UserHistoryActivity.class.getName()));

    }


    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}
