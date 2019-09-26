package com.wirelessiths.dal;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

import com.wirelessiths.handler.ListBookingHandler;
import org.junit.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
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
    public void getByScooterIdNoFilter() {

        System.out.println("getByscooterId query: ");
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
        list.forEach(System.out::println);
        assertEquals(2, list.size());
    }

    @Test
    public void getByUserId() {

        System.out.println("getByUserId query: ");
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
        list.forEach(System.out::println);
        assertEquals(2, list.size());
    }

    @Test
    public void getByDate() {

        System.out.println("getBydate query: ");
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
        list.forEach(System.out::println);
        assertEquals(3, list.size());
    }


    @Test
    public void getByScooterIdAllFilters() {
        System.out.println("getByscooterId query: ");
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
        list.forEach(System.out::println);
        assertEquals(0, list.size());
    }

    @Test
    public void getByScooterIdFilterByDate() {
        System.out.println("getByscooterId filter by startDate query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }

    @Test
    public void getByScooterIdFilterByUserId() {
        System.out.println("getByscooterId filter by userId query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }

    @Test
    public void getByDateAllFilters() {
        System.out.println("getByDate all filters query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }


    @Test
    public void getByDateFilterByUser() {

        System.out.println("getByDate Filter by user query: ");
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
        list.forEach(System.out::println);
        assertEquals(2, list.size());

    }

    @Test
    public void getByDateFilterByScooter() {

        System.out.println("getByDate Filter by scooter query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserAllFilters() {
        System.out.println("getByUserId Filter by user query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserFilterByScooterId() {
        System.out.println("getByUserId Filter by scooterId query: ");
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
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserFilterByDate() {
        System.out.println("getByUserId Filter by bookingDate query: ");
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
        list.forEach(System.out::println);
        assertEquals(2, list.size());
    }
}