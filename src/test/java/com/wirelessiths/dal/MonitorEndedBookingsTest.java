package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static junit.framework.TestCase.fail;

public class MonitorEndedBookingsTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "ended-bookings";

    @BeforeClass
    public static void setUpClientAndTable(){

        client = LocalDbHandler.createClient();
        mapperConfig = LocalDbHandler.createMapperConfig(tableName);
        LocalDbHandler.createTable(tableName, client);
    }

    @AfterClass
    public static void deleteTable(){
        LocalDbHandler.deleteTable(tableName, client);
    }

    @Test
    public void bookingsByEndtimeTest(){

        Booking b1 = new Booking(client, mapperConfig);
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);

        b1.setScooterId("1");
        b1.setEndTime(Instant.now().minusSeconds(60 * 5 + 5));
        b1.setBookingStatus(BookingStatus.CANCELLED);

        b2.setScooterId("2");
        b2.setEndTime(Instant.now().minusSeconds(60 * 6 + 5));
        b2.setBookingStatus(BookingStatus.VALID);

        b3.setScooterId("3");
        b3.setEndTime(Instant.now().minusSeconds(60 * 5 + 5));
        b3.setBookingStatus(BookingStatus.COMPLETED);

        b4.setScooterId("4");
        b4.setEndTime(Instant.now().minusSeconds(60 * 5 + 5));
        b4.setBookingStatus(BookingStatus.ACTIVE);

        b5.setScooterId("5");
        b5.setEndTime(Instant.now().minusSeconds(60 * 5 + 5));
        b5.setBookingStatus(BookingStatus.VALID);

        try{
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);

            List<Booking> endedBookings = b5.bookingsByEndTime();
            System.out.println(endedBookings.size());
            assert (endedBookings.size() == 3);
        }catch(Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }
}
