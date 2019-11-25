package com.example.beanleafteam29;

import com.google.firestore.v1.StructuredQuery;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderMenuActivityTest {

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