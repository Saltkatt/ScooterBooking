package com.wirelessiths.dal;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.wirelessiths.dal.trip.Trip;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

    public class BookingTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table";

    private static int maxDuration = 7200;
    private static int buffer = 300;
    private static int maxBookings = 3;


    @BeforeClass
    public static void setUpClientAndTable(){

        client = LocalDbHandler.createClient();
        mapperConfig = LocalDbHandler.createMapperConfig(tableName);
        LocalDbHandler.deleteTable(tableName, client);
        LocalDbHandler.createTable(tableName, client);
        populateForOkValidationTest();
        populateForFailValidationTest();
    }


    @AfterClass
    public static void deleteTable(){
       // LocalDbHandler.deleteTable(tableName, client);
    }


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
        }
        catch (Exception err) {
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

        public static void populateForOkValidationTest(){
        System.out.println("adding passing test cases to table..");
        Booking b1 = new Booking(client, mapperConfig );
        Booking b2 = new Booking(client, mapperConfig);

        b1.setScooterId("3");
        b1.setUserId("ok-cases");
        b1.setStartTime(Instant.parse("2019-09-02T13:20:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T13:45:00.000Z"));
        b1.setBookingStatus(BookingStatus.VALID);


        b2.setScooterId("3");
        b2.setUserId("ok-cases");
        b2.setStartTime(Instant.parse("2019-09-02T15:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-02T15:35:00.000Z"));
        b2.setBookingStatus(BookingStatus.VALID);


        try{
            b1.save(b1);
            b2.save(b2);
        }catch(Exception e){
            System.out.println("error in populateForOkValidationTest()100");
            System.out.println("msg: " + e.getMessage());
            fail();
        }
    }


    @Test
    public void bookingLogicValidationPassTest(){
        Booking testCase = new Booking(client, mapperConfig);

        testCase.setScooterId("3");
        testCase.setUserId("before-and-after");
        testCase.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        testCase.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        testCase.setBookingStatus(BookingStatus.VALID);


        try{
            List<Booking> bookings = testCase.validateBooking(testCase, maxDuration, buffer );
            System.out.println("pass test bookings:");
            bookings.forEach(System.out::println);
            System.out.println("pass test bookings.size(): " + bookings.size());
            assert(bookings.size() == 0);
        }catch(Exception e){
            System.out.println("error in bookingLogicValidationPassTest()");
            System.out.println(e.getMessage());
            fail();
        }
    }

    //@Before
    public static void populateForFailValidationTest(){
        System.out.println("adding fail validation test cases to table..");

        Booking b1 = new Booking(client, mapperConfig);
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);
        Booking b6 = new Booking(client, mapperConfig);
        Booking b7 = new Booking(client, mapperConfig);



        b1.setScooterId("2");
        b1.setUserId("over");
        b1.setBookingId("100");
        b1.setStartTime(Instant.parse("2019-09-02T13:30:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T15:45:00.000Z"));
        b1.setBookingStatus(BookingStatus.ACTIVE);

        b2.setScooterId("2");
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
        b7.setBookingStatus(BookingStatus.VALID);


        try{
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);
            b6.save(b6);
            //b7.save(b7);
        }catch(Exception e){
            System.out.println("error in populateForFailValidationTest()");
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void bookingLogicValidationFailTest(){
        Booking testCase = new Booking(client, mapperConfig);

        testCase.setScooterId("2");
        testCase.setUserId("testCase");
        testCase.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        testCase.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        //testCase.setBookingStatus(BookingStatus.VALID);


        try{
            List<Booking> bookings = testCase.validateBooking(testCase, maxDuration, buffer);
            System.out.println("fail test bookings.size(): " + bookings.size());

            bookings.forEach(System.out::println);
            System.out.println(bookings.size());
            assert(bookings.size() == 4);
        }catch(Exception e){
            System.out.println("error in bookingLogicValidationFailTest()");
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void bookingsByEndTimeTest(){
        LocalDate today = LocalDate.parse(Instant.now().toString().split("T")[0]);
        Instant now = Instant.now();

        Booking b1 = new Booking(client, mapperConfig);
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);
        Booking b6 = new Booking(client, mapperConfig);
        Booking b7 = new Booking(client, mapperConfig);


        b1.setStartTime(now.minusSeconds(60 * 60 + 30 * 60));
        b1.setEndTime(now.minusSeconds(60 * 10 + 30));
        b1.setScooterId("123");
        b1.setBookingStatus(BookingStatus.VALID);


        b2.setStartTime(now.minusSeconds(60 * 60));
        b2.setEndTime(now.minusSeconds(60 * 10 + 50));
        b2.setScooterId("1234");
        b2.setBookingStatus(BookingStatus.VALID);


        b3.setStartTime(now.minusSeconds(60 * 60 + 20 * 60));
        b3.setEndTime(now.minusSeconds(60 * 7 + 10));
        b3.setScooterId("12345");
        b3.setBookingStatus(BookingStatus.ACTIVE);


        b4.setStartTime(now.minusSeconds(60 * 60));
        b4.setEndTime(now.minusSeconds(60 * 4 + 30));
        b4.setScooterId("100");
        b4.setBookingStatus(BookingStatus.ACTIVE);


        b5.setStartTime(now.minusSeconds(60 * 60 + 20 * 60));
        b5.setEndTime(now.minusSeconds(60 * 5 + 10));
        b5.setScooterId("200");
        b5.setBookingStatus(BookingStatus.VALID);

        b6.setStartTime(now.minusSeconds(60 * 60));
        b6.setEndTime(now.minusSeconds(60 * 5 + 30));
        b6.setScooterId("1000");
        b6.setBookingStatus(BookingStatus.CANCELLED);


        b7.setStartTime(now.minusSeconds(60 * 60));
        b7.setEndTime(now.minusSeconds(60 * 10));
        b7.setScooterId("10000");
        b7.setBookingStatus(BookingStatus.COMPLETED);



        try{
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);
            b6.save(b6);
            b7.save(b7);

            List<Booking> bookings = b1.bookingsByEndTime();
            System.out.println("today: " + today);
            System.out.println("now: " + Instant.now().toString());
            System.out.println("ending bookings: " + bookings.size());
                assert(bookings.size() == 1);//todo:double check this
            //TODO: if start time and end time does not occur at same date, the dao method fails, solve this?
            bookings.forEach( p -> {
                System.out.println("now: " + Instant.now().toString());
                System.out.println("ending booking: " + p);
            });

        }catch(Exception e){
            System.out.println("error in bookingsbyendtime: " + e.getMessage());
            fail();
        }
    }


    @Test
    public void daoCrudTest(){
        Booking booking = new Booking(client, mapperConfig );

        booking.setScooterId("1");
        booking.setUserId("test-1");
        booking.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));

        try{
            //create booking
            Booking savedBooking = booking.save(booking);
            //read by bookingId
            assertEquals(savedBooking, booking.get(savedBooking.getBookingId()));
            //update
            String newUserId = "test-2";
            booking.setUserId(newUserId);
            booking.update(booking);
            assertEquals(booking.getUserId(), newUserId);

            //delete booking
            assert(booking.delete(savedBooking.getBookingId()));
            //delete deleted booking
            assert(!booking.delete(savedBooking.getBookingId()));
        }catch(Exception e){
            fail();

            System.out.println(e.getMessage());

        }

    }
    @Test
    public void addTrips(){
        Instant now = Instant.now();
        Trip trip = new Trip();
        Booking booking = new Booking(client, mapperConfig);
        booking.setStartTime(now.minusSeconds(60 * 60 + 20 * 60));
        booking.setEndTime(now.minusSeconds(60 * 10 + 10));
        booking.setScooterId("12345");
        booking.setBookingStatus(BookingStatus.ACTIVE);

        Booking bookingNoTrip = new Booking(client, mapperConfig);
        bookingNoTrip.setStartTime(now.minusSeconds(60 * 60 + 30 * 60));
        bookingNoTrip.setEndTime(now.minusSeconds(60 * 10 + 10));
        bookingNoTrip.setScooterId("123457");
        bookingNoTrip.setBookingStatus(BookingStatus.ACTIVE);


        try{
            bookingNoTrip.save(bookingNoTrip);
            bookingNoTrip.get(bookingNoTrip.getBookingId());

            System.out.println("in ad trip: " + booking.getBookingId());
            booking.getTrips().add(trip);
            booking.save(booking);
            System.out.println(booking);

            String bookingId = booking.getBookingId();
            Booking b2 = booking.get(bookingId);
            System.out.println("b2: " + b2);
        }catch(Exception e){
            System.out.println(e.getMessage());
            fail();
        }
    }
<<<<<<< HEAD

=======
>>>>>>> add working query tests, clean up tests, clean up MonitorEndedBooking part1
}
