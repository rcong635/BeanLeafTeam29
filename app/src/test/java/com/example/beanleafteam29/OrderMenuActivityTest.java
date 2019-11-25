package com.example.beanleafteam29;

import com.google.firestore.v1.StructuredQuery;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderMenuActivityTest {

    @Test
    public void testSettersAndGetters() {
        OrderMenuActivity.setCoordinates(0, 0, 0, 0);
        assertEquals(0, OrderMenuActivity.getUserLat(), 0);
        assertEquals(0, OrderMenuActivity.getUserLng(), 0);
        assertEquals(0, OrderMenuActivity.getLocationLat(), 0);
        assertEquals(0, OrderMenuActivity.getLocationLng(), 0);
        OrderMenuActivity.setCoordinates(10, 9, 8, 7);
        assertEquals(10, OrderMenuActivity.getUserLat(), 0);
        assertEquals(9, OrderMenuActivity.getUserLng(), 0);
        assertEquals(8, OrderMenuActivity.getLocationLat(), 0);
        assertEquals(7, OrderMenuActivity.getLocationLng(), 0);
        OrderMenuActivity.setCoordinates(268, 45, 239, 345);
        assertEquals(268, OrderMenuActivity.getUserLat(), 0);
        assertEquals(45, OrderMenuActivity.getUserLng(), 0);
        assertEquals(239, OrderMenuActivity.getLocationLat(), 0);
        assertEquals(345, OrderMenuActivity.getLocationLng(), 0);
        OrderMenuActivity.setCoordinates(124, 324, 356, 333);
        assertEquals(124, OrderMenuActivity.getUserLat(), 0);
        assertEquals(324, OrderMenuActivity.getUserLng(), 0);
        assertEquals(356, OrderMenuActivity.getLocationLat(), 0);
        assertEquals(333, OrderMenuActivity.getLocationLng(), 0);
        OrderMenuActivity.setCoordinates(888, 235, 745, 111);
        assertEquals(888, OrderMenuActivity.getUserLat(), 0);
        assertEquals(235, OrderMenuActivity.getUserLng(), 0);
        assertEquals(745, OrderMenuActivity.getLocationLat(), 0);
        assertEquals(111, OrderMenuActivity.getLocationLng(), 0);
    }

    @Test
    public void testCaffeineToLong() {
        assertEquals(150, OrderMenuActivity.caffeineToLong("150mg"));
        assertEquals(10, OrderMenuActivity.caffeineToLong("10mg"));
        assertEquals(200, OrderMenuActivity.caffeineToLong("200mg"));
        assertEquals(50, OrderMenuActivity.caffeineToLong("50mg"));
        assertEquals(15, OrderMenuActivity.caffeineToLong("15mg"));
        assertEquals(20, OrderMenuActivity.caffeineToLong("20mg"));
        assertEquals(300, OrderMenuActivity.caffeineToLong("300mg"));
        assertEquals(75, OrderMenuActivity.caffeineToLong("75mg"));
        assertEquals(333, OrderMenuActivity.caffeineToLong("333mg"));
        assertEquals(0, OrderMenuActivity.caffeineToLong("0mg"));
    }

    @Test
    public void testCheckDistance() {
        OrderMenuActivity.setCoordinates(0, 0, 0, 0);
        assertEquals(0, OrderMenuActivity.checkDistance(), 0);
        OrderMenuActivity.setCoordinates(0, 0, .01, .01);
        assertEquals(1572.53, OrderMenuActivity.checkDistance(), 1);
        OrderMenuActivity.setCoordinates(0, 0, .005, .005);
        assertEquals(786.27, OrderMenuActivity.checkDistance(), .5);
        OrderMenuActivity.setCoordinates(0, 0, -.01, -.01);
        assertEquals(1572.53, OrderMenuActivity.checkDistance(), 1);
        OrderMenuActivity.setCoordinates(0, 0, -.005, .005);
        assertEquals(786.27, OrderMenuActivity.checkDistance(), .5);
        OrderMenuActivity.setCoordinates(10, 0, 9.99, .01);
        assertEquals(1560.65, OrderMenuActivity.checkDistance(), 1);
        OrderMenuActivity.setCoordinates(100, 100, 100, 100);
        assertEquals(0, OrderMenuActivity.checkDistance(), 0);
    }

}