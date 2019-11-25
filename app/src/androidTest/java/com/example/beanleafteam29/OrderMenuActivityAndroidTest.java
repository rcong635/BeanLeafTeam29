package com.example.beanleafteam29;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class OrderMenuActivityAndroidTest {

    @Rule
    public ActivityTestRule<OrderMenuActivity> mActivityRule = new ActivityTestRule<>(OrderMenuActivity.class, false, false);
    @Rule
    public IntentsTestRule<OrderMenuActivity> mIntentsRule = new IntentsTestRule<>(OrderMenuActivity.class);

    @Test
    public void testMenuItemsVisible() {
        Intent intent = new Intent();
        intent.putExtra("userLat", 34.0202);
        intent.putExtra("userLng", -118.2858);
        intent.putExtra("locationLat", 34.0257138);
        intent.putExtra("locationLng", -118.2861568);
        intent.putExtra("locationID", "sbgsYTCIhiGR6pUQ5Ids");
        intent.putExtra("locationName", "Cafe Dulce");
        mActivityRule.launchActivity(intent);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withText("Menu")).check(matches(isDisplayed()));
        Espresso.onView(withText("Organic Assam Sewper Milk Tea Latte")).check(matches(isDisplayed()));
        Espresso.onView(withText("210 mg caffeine")).check(matches(isDisplayed()));
        Espresso.onView(withText("$29.99")).check(matches(isDisplayed()));

        Espresso.onView(withText("Organic Masala Chai Latte")).check(matches(isDisplayed()));
        Espresso.onView(withText("130 mg caffeine")).check(matches(isDisplayed()));
        Espresso.onView(withText("$24.99")).check(matches(isDisplayed()));

        Espresso.onView(withText("Organic White Pomegranate")).check(matches(isDisplayed()));
        Espresso.onView(withText("180 mg caffeine")).check(matches(isDisplayed()));
        Espresso.onView(withText("$19.99")).check(matches(isDisplayed()));

        Espresso.onView(withText("Test")).check(matches(isDisplayed()));
        Espresso.onView(withText("111 mg caffeine")).check(matches(isDisplayed()));
        Espresso.onView(withText("$9.99")).check(matches(isDisplayed()));

        Espresso.onView(withText("120 mg caffeine")).check(matches(isDisplayed()));
        Espresso.onView(withText("$12.99")).check(matches(isDisplayed()));
    }
}