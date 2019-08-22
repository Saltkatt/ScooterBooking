package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.wirelessiths.dal.Booking;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;


public class BookingTest {

    private String bookingId;
    private String scooterId;
    private String userId;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Booking book = new Booking();


    /**
     *  Test to verify booking content.
     *  todo: unfinished test.
     */
    private void verifyBookingItem(Item body){
        assertTrue(body.hasAttribute("bookingId"));
        String bookingId = body.getString("bookingId");
        assertNotNull(bookingId);
        assertTrue(bookingId.contains("-"));

        assertTrue(body.hasAttribute("scooterId"));
        String scooterId = body.getString("scooterId");
        assertNotNull(scooterId);
        assertTrue(scooterId.contains("-"));

        assertTrue(body.hasAttribute("userId"));
        String userId = body.getString("userId");
        assertEquals("foo", userId);

        assertTrue(body.hasAttribute("message"));
        String message = body.getString("message");

        /*LocalDateTime startTime = LocalDateTime.now();
        assertThat(startTime).isBetween(startTime.minusSeconds(1), startTime.plusSeconds(1))
                .isBetween(startTime, startTime.plusSeconds(1))
                .isBetween(startTime.minusSeconds(1), startTime)
                .isBetween(startTime, startTime)
                .isStrictlyBetween(startTime.minusSeconds(1), startTime.plusSeconds(1));*/


    }


}
