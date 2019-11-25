package com.example.beanleafteam29;

import android.content.Intent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderMenuActivityTest {

    @Rule
    public ActivityTestRule<OrderMenuActivity> mActivityRule = new ActivityTestRule(OrderMenuActivity.class, false, false);
    @Rule
    public IntentsTestRule<OrderMenuActivity> mIntentsRule = new IntentsTestRule<>(OrderMenuActivity.class);

    @Test
    public void testMenuItemsVisible() {
        Intent intent = new Intent();
        intent.putExtra("locationID", "sbgsYTCIhiGR6pUQ5Ids");
        intent.putExtra("locationName", "Cafe Dulce");
        mActivityRule.launchActivity(intent);
        int i = 0;
    }
}