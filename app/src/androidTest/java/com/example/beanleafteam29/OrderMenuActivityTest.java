package com.example.beanleafteam29;

import androidx.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderMenuActivityTest {

    @Rule
    public ActivityTestRule<OrderMenuActivity> mActivityRule = new ActivityTestRule(OrderMenuActivity.class);

    @Before
    public void setUp() throws Exception {
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
        assertEquals(150, mActivityRule.getActivity().caffeineToLong("150mg"));
        assertEquals(10, mActivityRule.getActivity().caffeineToLong("10mg"));
        assertEquals(200, mActivityRule.getActivity().caffeineToLong("200mg"));
        assertEquals(50, mActivityRule.getActivity().caffeineToLong("50mg"));
        assertEquals(15, mActivityRule.getActivity().caffeineToLong("15mg"));
        assertEquals(20, mActivityRule.getActivity().caffeineToLong("20mg"));
        assertEquals(300, mActivityRule.getActivity().caffeineToLong("300mg"));
        assertEquals(75, mActivityRule.getActivity().caffeineToLong("75mg"));
        assertEquals(333, mActivityRule.getActivity().caffeineToLong("333mg"));
        assertEquals(0, mActivityRule.getActivity().caffeineToLong("0mg"));
    }

    @Test
    public void checkDistance() {
    }
}