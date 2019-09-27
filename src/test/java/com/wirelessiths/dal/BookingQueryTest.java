package com.wirelessiths.dal;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.wirelessiths.handler.ListBookingHandler;
import org.junit.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BookingQueryTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "query-test-table";


    @BeforeClass
    public static void setUpClientAndTable() {
        //  BookingTest.startLocalDynamoDB();
//        createClient();
//        createTable();

        client = LocalDbHandler.createClient();
        mapperConfig = LocalDbHandler.createMapperConfig(tableName);
        LocalDbHandler.deleteTable(tableName, client);
        LocalDbHandler.createTable(tableName, client);
        populateForQueryTests();
    }

    @AfterClass
    public static void deleteTableAfterTests() {
        LocalDbHandler.deleteTable(tableName, client);
        //deleteTable();
    }


    private static void populateForQueryTests() {

        System.out.println("adding query test cases to table..");
        Booking b1 = new Booking(client, mapperConfig);
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);
        Booking b6 = new Booking(client, mapperConfig);

        b1.setScooterId("1");
        b1.setUserId("a");
        b1.setStartTime(Instant.parse("2019-09-03T13:20:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-03T13:45:00.000Z"));
        b1.setStartDate(LocalDate.parse("2019-09-03"));

        b2.setScooterId("2");
        b2.setUserId("b");
        b2.setStartTime(Instant.parse("2019-09-04T15:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-04T15:35:00.000Z"));
        b2.setStartDate(LocalDate.parse("2019-09-04"));

        b3.setScooterId("1");
        b3.setUserId("c");
        b3.setStartTime(Instant.parse("2019-09-03T15:10:00.000Z"));
        b3.setEndTime(Instant.parse("2019-09-03T15:35:00.000Z"));
        b3.setStartDate(LocalDate.parse("2019-09-03"));

        b4.setScooterId("4");
        b4.setUserId("c");
        b4.setStartTime(Instant.parse("2019-09-03T13:20:00.000Z"));
        b4.setEndTime(Instant.parse("2019-09-03T13:45:00.000Z"));
        b4.setStartDate(LocalDate.parse("2019-09-03"));

        b5.setScooterId("4");
        b5.setUserId("d");
        b5.setStartTime(Instant.parse("2019-09-04T13:20:00.000Z"));
        b5.setEndTime(Instant.parse("2019-09-04T13:45:00.000Z"));
        b5.setStartDate(LocalDate.parse("2019-09-04"));


        try {
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);

        } catch (Exception e) {
            System.out.println("error in getTrips for query test");
            System.out.println("msg: " + e.getMessage());
        }

    }
    //Tests for query methods

    @Test
    public void scooterId() {

        System.out.println("scooterId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("scooterId", "4");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, list.size());
    }

    @Test
    public void userId() {

        System.out.println("userId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("userId", "c");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, list.size());
    }

    @Test
    public void StartDate() {

        System.out.println("StartDate query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-03");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(3, list.size());
    }


    @Test
    public void scooterIdUserIdStartDate() {
        System.out.println("scooterIdUserIdStartDate : ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("scooterId", "2");
        queryparams.put("userId", "c");
        queryparams.put("startDate", "2019-09-03");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(0, list.size());
    }

    @Test
    public void scooterIdStartDate() {
        System.out.println("scooterIdStartDate query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("scooterId", "2");
        queryparams.put("startDate", "2019-09-04");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());
    }

    @Test
    public void scooterIdUserId() {
        System.out.println("scooterIdUserId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("scooterId", "4");
        queryparams.put("userId", "c");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());
    }

    @Test
    public void startDateScooterIdUserId0() {
        System.out.println("startDateScooterIdUserId0 query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-03");
        queryparams.put("scooterId", "4");
        queryparams.put("userId", "c");

        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());
    }


    @Test
    public void startDateUserId() {

        System.out.println("startDateUserId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-03");
        queryparams.put("userId", "c");

        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, list.size());

    }

    @Test
    public void startDateScooterId() {

        System.out.println("startDateScooterId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-04");
        queryparams.put("scooterId", "2");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());

    }

    @Test
    public void startDateScooterIdUserId() {
        System.out.println("startDateScooterIdUserId query: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-03");
        queryparams.put("scooterId", "1");
        queryparams.put("userId", "c");

        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());

    }

    @Test
    public void userIdScooterId() {
        System.out.println("userId and ScooterId : ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("userId", "c");
        queryparams.put("scooterId", "1");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());

    }

    @Test
    public void userIdDate() {
        System.out.println("userid and date: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("userId", "c");
        queryparams.put("startDate", "2019-09-03");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, list.size());
    }

    @Test
    public void nullQueryParamsgetsAll() {
        System.out.println("userid and date: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        try {
            list = listBookingHandler.retrieveBookings(null, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(5, list.size());
    }

    @Test
    public void emptyQueryParamsgetsAll() {
        System.out.println("userid and date: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(5, list.size());
    }

    @Test
    public void unknownQueryParamIgnored() {
        System.out.println("userid and date: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("Rebookable", "true");
        queryparams.put("cool", "no");
        queryparams.put("userId", "c");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, list.size());
    }

    @Test
    public void allQueryParamsUnkownGetsAll() {
        System.out.println("userid and date: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("Rebookable", "true");
        queryparams.put("cool", "no");
        queryparams.put("Flying", "yes");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(5, list.size());
    }

    @Test
    public void reWritesDateToStartDate() {
        System.out.println("date rewrite to startdate: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("date", "2019-09-03");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(3, list.size());
    }

    @Test  (expected = DateTimeParseException.class)
    public void wronglyFormattedValues() {
        System.out.println("date rewrite to startdate: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "tja");
        queryparams.put("scooterId", "1");
        queryparams.put("userId", "c");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test  (expected = AmazonDynamoDBException.class)
    public void ValuesEmptyThrowsDynamoDBException() {
        System.out.println("date rewrite to startdate: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("scooterId", "");
        queryparams.put("userId", "");
        try {
            list = listBookingHandler.retrieveBookings(queryparams, booking);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void manualTestForScooterIdQuery() {
        System.out.println("date rewrite to startdate: ");
        Booking booking = new Booking(client, mapperConfig);
        List<Booking> list = new ArrayList<>();
        ListBookingHandler listBookingHandler = new ListBookingHandler();
        Map<String, String> queryparams = new HashMap<>();
        queryparams.put("startDate", "2019-09-03");
        queryparams.put("userId", "c");
        try {
            list = booking.bookingsByScooterId("1", queryparams);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, list.size());
    }







}