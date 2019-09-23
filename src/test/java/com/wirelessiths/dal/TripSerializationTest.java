package com.wirelessiths.dal;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wirelessiths.dal.trip.Trip;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.HttpRequest;



import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.fail;

public class TripSerializationTest {



    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table-serialization";

    private static Dotenv dotenv = Dotenv.load();
    private static String baseUrl = dotenv.get("BASE_URL");
    private static String tripEndpoint = dotenv.get("TRIP_ENDPOINT");
    private static String authHeader = dotenv.get("AUTH");
    private static String vehicleId = dotenv.get("SCOOTER_ID");
    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);




    @BeforeClass

    public static void create(){
      LocalDbHandler.createClient();
      LocalDbHandler.createTable(tableName, client);

    }

    @AfterClass
    public static void deleteTable() {
        LocalDbHandler.deleteTable(tableName, client);


    }

    public static List<Trip> getTrips() {


        String url = String.format("%s/%s%s", baseUrl, vehicleId, tripEndpoint);
        String queryUrl = url + "?startDate=" + vehicleId;

        try{
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();




            Request request = new Request
                    .Builder()
                    .addHeader("Authorization", authHeader)
                    .url(queryUrl).build();


            Response response = httpClient.newCall(request).execute();

            ArrayNode trips = (ArrayNode) objectMapper.readTree(response.body().string())
                    .path("trip_overview_list");

            return objectMapper.convertValue(trips, new TypeReference<List<Trip>>() {});

        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return Collections.emptyList();
    }

    @Test
<<<<<<< HEAD
    public void serializeTripTest() {
<<<<<<< HEAD

=======
=======
    public void serializeTripTest(){
>>>>>>> fix looping bug in MonitorEndedBookingsTemp, change name from MonitorEndedBookingsFake to ..BookingsTemp
>>>>>>> fix looping bug in MonitorEndedBookingsTemp, change name from MonitorEndedBookingsFake to ..BookingsTemp
        Booking booking = new Booking(client, mapperConfig);
        Booking booking3 = new Booking(client, mapperConfig);


        booking.setScooterId("100");
        booking.setUserId("test-append-trip");
        booking.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        booking.setBookingStatus(BookingStatus.VALID);

        booking3.setScooterId("3");
        booking3.setUserId("test-append-trip3");
        booking3.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        booking3.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        booking3.setBookingStatus(BookingStatus.VALID);

        List<Trip> newTrips = getTrips();


        assert (newTrips != null && !newTrips.isEmpty());

        try {
            booking3.getTrips().add(newTrips.get(0));
            System.out.println("3,1: " + booking3);
            booking3.save(booking3);

            booking3.get(booking3.getBookingId());
            newTrips.forEach(trip -> booking.getTrips().add(trip));
            booking.save(booking);
            System.out.println("booking saved: " + booking);
            System.out.println("3,2: " + booking3);

            System.out.println("booking id: " + booking.getBookingId());
            Booking booking2 = booking.get(booking.getBookingId());
            assert (!booking2.getTrips().isEmpty());
            System.out.println("booking2: " + booking2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

    }
}

