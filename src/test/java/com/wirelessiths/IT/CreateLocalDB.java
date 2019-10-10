package com.wirelessiths.IT;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.BookingStatus;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CreateLocalDB {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table";

    private static int maxDuration = 7200;
    private static int buffer = 300;
    private static int maxBookings = 3;


    @BeforeClass
    public static void setUpClientAndTable() {
        client = BookingTestBase.createClient();
        mapperConfig = BookingTestBase.createMapperConfig(tableName);
        BookingTestBase.createTable(tableName, client);
        //populateForOkValidationTest();
        //populateForFailValidationTest();
    }


  /*  @AfterClass
    public static void deleteTable(){
    BookingTestBase.deleteTable(tableName, client);
}*/


    public static void startLocalDynamoDB() {
        String line = "";
        try {
            Process p = Runtime.getRuntime().exec("docker ps --filter ancestor=amazon/dynamodb-local");
            BufferedReader bri = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader
                    (new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();
            System.out.println("Done.");
        } catch (Exception err) {
            err.printStackTrace();
        }
        if (!line.contains("amazon/dynamodb-local")) {
            Runtime rt = Runtime.getRuntime();
            try {
                Process pr = rt.exec("docker run -d -p 8000:8000 amazon/dynamodb-local");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void populateForOkValidationTest() {
        System.out.println("adding passing test cases to table..");
        Booking b1 = new Booking(client, mapperConfig);
        Booking b2 = new Booking(client, mapperConfig);

        b1.setScooterId("3");
        b1.setUserId("ok-cases");
        b1.setStartTime(Instant.parse("2019-09-02T13:20:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T13:45:00.000Z"));
        b1.setBookingStatus(BookingStatus.VALID);


        /*b2.setScooterId("3");
        b2.setUserId("ok-cases");
        b2.setStartTime(Instant.parse("2019-09-02T15:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-02T15:35:00.000Z"));
        b2.setBookingStatus(BookingStatus.VALID);*/


        try {
            b1.save(b1);
            //b2.save(b2);
        } catch (Exception e) {
            System.out.println("error in populateForOkValidationTest()100");
            System.out.println("msg: " + e.getMessage());
            fail();
        }
    }


    //@Before
    public static void populateForFailValidationTest() {
        System.out.println("adding fail validation test cases to table..");

        Booking b1 = new Booking(client, mapperConfig);
       /* Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);
        Booking b6 = new Booking(client, mapperConfig);
        Booking b7 = new Booking(client, mapperConfig);*/


        b1.setScooterId("2");
        b1.setUserId("over");
        b1.setBookingId("100");
        b1.setStartTime(Instant.parse("2019-09-02T13:30:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T15:45:00.000Z"));
        b1.setBookingStatus(BookingStatus.ACTIVE);

        /*b2.setScooterId("2");
        b2.setUserId("before-in");
        b2.setStartTime(Instant.parse("2019-09-02T11:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-02T14:35:00.000Z"));
        b2.setBookingStatus(BookingStatus.VALID);


        b3.setScooterId("2");
        b3.setUserId("after-out");
        b3.setStartTime(Instant.parse("2019-09-02T14:30:00.000Z"));
        b3.setEndTime(Instant.parse("2019-09-02T15:30:00.000Z"));
        b3.setStartDate(LocalDate.parse("2019-09-02"));
        b3.setBookingStatus(BookingStatus.VALID);


        b4.setScooterId("2");
        b4.setUserId("between");
        b4.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        b4.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        b4.setBookingStatus(BookingStatus.ACTIVE);


        b5.setScooterId("2");
        b5.setUserId("ok-cases");
        b5.setStartTime(Instant.parse("2019-09-02T13:20:00.000Z"));
        b5.setEndTime(Instant.parse("2019-09-02T13:45:00.000Z"));
        b5.setBookingStatus(BookingStatus.CANCELLED);


        b6.setScooterId("2");
        b6.setUserId("ok-cases");
        b6.setStartTime(Instant.parse("2019-09-02T15:10:00.000Z"));
        b6.setEndTime(Instant.parse("2019-09-02T15:35:00.000Z"));
        b6.setBookingStatus(BookingStatus.VALID);


        b7.setScooterId("2");//-------------------------------
        b7.setUserId("after-out2");
        b7.setStartTime(Instant.parse("2019-09-02T14:35:00.000Z"));
        b7.setEndTime(Instant.parse("2019-09-02T15:30:00.000Z"));
        b7.setBookingStatus(BookingStatus.VALID);*/


        try {
            b1.save(b1);
//            b2.save(b2);
//            b3.save(b3);
//            b4.save(b4);
//            b5.save(b5);
//            b6.save(b6);
            //b7.save(b7);
        } catch (Exception e) {
            System.out.println("error in populateForFailValidationTest()");
            System.out.println(e.getMessage());
            fail();
        }
    }


    @Test
    public void bookingLogicValidationPassTest() {
        Booking testCase = new Booking(client, mapperConfig);

        testCase.setScooterId("3");
        testCase.setUserId("before-and-after");
        testCase.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        testCase.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        testCase.setBookingStatus(BookingStatus.VALID);


        try {
            List<Booking> bookings = testCase.validateBooking(testCase, maxDuration, buffer);
            System.out.println("pass test bookings:");
            bookings.forEach(System.out::println);
            System.out.println("pass test bookings.size(): " + bookings.size());
            assert (bookings.size() == 0);
        } catch (Exception e) {
            System.out.println("error in bookingLogicValidationPassTest()");
            System.out.println(e.getMessage());
            fail();
        }
    }

}
