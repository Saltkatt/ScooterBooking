package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.wirelessiths.dal.DynamoDBAdapter;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

import static org.junit.Assert.*;


public class BookingTest {


    /**
     *  Test to verify booking content.
     *  todo: unfinished test.
     */
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
