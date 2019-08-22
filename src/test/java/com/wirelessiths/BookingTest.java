package com.wirelessiths;


import com.amazonaws.services.dynamodbv2.document.Item;
import org.junit.Test;


import static org.junit.Assert.*;


public class BookingTest {


    /**
     *  Test to verify booking content.
     *  todo: unfinished test.
     */
    @Test
    private void verifyBookingItem(Item body){
        assertTrue(body.hasAttribute("bookingId"));
        String bookingId = body.getString("bookingId");
        assertNotNull(bookingId);
        assertTrue(bookingId.contains("-"));

        assertTrue(body.hasAttribute("userId"));
        String userId = body.getString("userId");
        assertEquals("foo", userId);

        assertTrue(body.hasAttribute("message"));
        String message = body.getString("message");
        
    }
}
