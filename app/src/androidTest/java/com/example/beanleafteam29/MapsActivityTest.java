package com.example.beanleafteam29;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapsActivityTest {

    MapsActivity mActivity;

    @Rule
    public ActivityTestRule<MapsActivity> mActivityRule = new ActivityTestRule(MapsActivity.class);

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void testConvertToPixels() {
        assertEquals(0, MapsActivity.convertToPixels(mActivity.getBaseContext(), 0));
        assertEquals(35, MapsActivity.convertToPixels(mActivity.getBaseContext(), 10));
        assertEquals(350, MapsActivity.convertToPixels(mActivity.getBaseContext(), 100));
        assertEquals(3500, MapsActivity.convertToPixels(mActivity.getBaseContext(), 1000));
        assertEquals(1166, MapsActivity.convertToPixels(mActivity.getBaseContext(), 333));
        assertEquals(1985, MapsActivity.convertToPixels(mActivity.getBaseContext(), 567));
        assertEquals(46, MapsActivity.convertToPixels(mActivity.getBaseContext(), 13));
        assertEquals(28, MapsActivity.convertToPixels(mActivity.getBaseContext(), 8));
        assertEquals(11, MapsActivity.convertToPixels(mActivity.getBaseContext(), 3));
        assertEquals(4, MapsActivity.convertToPixels(mActivity.getBaseContext(), 1));
    }
}