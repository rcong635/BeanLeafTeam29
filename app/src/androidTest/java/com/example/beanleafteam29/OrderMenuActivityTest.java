package com.example.beanleafteam29;

import androidx.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderMenuActivityTest {

    OrderMenuActivity mActivity;

    @Rule
    public ActivityTestRule<OrderMenuActivity> mActivityRule = new ActivityTestRule(OrderMenuActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onBuyButtonClicked() {
    }

    @Test
    public void caffeineToLong() {
        assertEquals(150, mActivity.caffeineToLong("150mg"));
        assertEquals(10, mActivity.caffeineToLong("10mg"));
        assertEquals(200, mActivity.caffeineToLong("200mg"));
        assertEquals(50, mActivity.caffeineToLong("50mg"));
        assertEquals(15, mActivity.caffeineToLong("15mg"));
        assertEquals(20, mActivity.caffeineToLong("20mg"));
        assertEquals(300, mActivity.caffeineToLong("300mg"));
        assertEquals(75, mActivity.caffeineToLong("75mg"));
        assertEquals(333, mActivity.caffeineToLong("333mg"));
        assertEquals(0, mActivity.caffeineToLong("0mg"));
    }

    @Test
    public void checkDistance() {
        mActivity.setCoordinates(0, 0, 0, 0);
        assertEquals(0, mActivity.checkDistance(), 0);
        mActivity.setCoordinates(0, 0, .01, .01);
        assertEquals(1572.53, mActivity.checkDistance(), 1);
        mActivity.setCoordinates(0, 0, .005, .005);
        assertEquals(786.27, mActivity.checkDistance(), .5);
        mActivity.setCoordinates(0, 0, -.01, -.01);
        assertEquals(1572.53, mActivity.checkDistance(), 1);
        mActivity.setCoordinates(0, 0, -.005, .005);
        assertEquals(786.27, mActivity.checkDistance(), .5);
        mActivity.setCoordinates(10, 0, 9.99, .01);
        assertEquals(1560.65, mActivity.checkDistance(), 1);
        mActivity.setCoordinates(100, 100, 100, 100);
        assertEquals(0, mActivity.checkDistance(), 0);
    }

}