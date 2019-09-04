package com.wirelessiths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.*;
import com.wirelessiths.handler.UpdateBookingHandler;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReturnScooterHandler {

    String jsonString = "{  \n" +
            "   \"scooterId\":\"189abd\",\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\",\n" +
            "   \"date\":\"2019-08-30\",\n" +
            "   \"tripStatus\":\"COMPLETED\"\n" +
            "}";
    String jsonStringNoDate = "{  \n" +
            "   \"scooterId\":\"189abd\",\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\",\n" +
            "   \"tripStatus\":\"COMPLETED\"\n" +
            "}";

    String nullJsonString = "{  \n" +
            "   \"scooterId\":null,\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\",\n" +
            "   \"tripStatus\":\"COMPLETED\"\n" +
            "}";



    @Test
    public void testFullJsonString() {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();

        try {
            updateBookingRequest =  mapper.treeToValue(jsonNode, UpdateBookingRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        LocalDateConverter converter = new LocalDateConverter();
        InstantConverter instantConverter = new InstantConverter();

        Booking booking = new Booking();
        booking.setTripStatus(TripStatus.WAITING_TO_START);
        booking.setUserId("123abc");
        booking.setDate(converter.unconvert("2019-08-25"));
        booking.setStartTime(instantConverter.unconvert("2019-08-30T15:00:36.739Z"));
        booking.setEndTime(instantConverter.unconvert("2019-08-30T16:00:36.739Z"));
        booking.setUserId("abcdgwhwer23");
        booking.setBookingId("myb00kingid");
        booking.setScooterId("scooterino124");


        UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);

       assertEquals(TripStatus.COMPLETED, booking.getTripStatus());
       assertEquals("83396a64-4a39-4c5f-b7e4-8e18b435b41e",booking.getUserId());



    }

    @Test
    public void testJsonStringWithMissingProperties() {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = mapper.readTree(jsonStringNoDate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();

        try {
            updateBookingRequest =  mapper.treeToValue(jsonNode, UpdateBookingRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(updateBookingRequest);

        LocalDateConverter converter = new LocalDateConverter();
        InstantConverter instantConverter = new InstantConverter();

        Booking booking = new Booking();
        booking.setTripStatus(TripStatus.WAITING_TO_START);
        booking.setUserId("123abc");
        booking.setDate(converter.unconvert("2019-08-25"));
        booking.setStartTime(instantConverter.unconvert("2019-08-30T15:00:36.739Z"));
        booking.setEndTime(instantConverter.unconvert("2019-08-30T16:00:36.739Z"));
        booking.setUserId("abcdgwhwer23");
        booking.setBookingId("myb00kingid");
        booking.setScooterId("scooterino124");



        UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);

        assertEquals("2019-08-25",booking.getDate().toString());



    }

    @Test
    public void testWithNullJsonString() {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = mapper.readTree(nullJsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();

        try {
            updateBookingRequest =  mapper.treeToValue(jsonNode, UpdateBookingRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        LocalDateConverter converter = new LocalDateConverter();
        InstantConverter instantConverter = new InstantConverter();

        Booking booking = new Booking();
        booking.setTripStatus(TripStatus.WAITING_TO_START);
        booking.setUserId("123abc");
        booking.setDate(converter.unconvert("2019-08-25"));
        booking.setStartTime(instantConverter.unconvert("2019-08-30T15:00:36.739Z"));
        booking.setEndTime(instantConverter.unconvert("2019-08-30T16:00:36.739Z"));
        booking.setUserId("abcdgwhwer23");
        booking.setBookingId("myb00kingid");
        booking.setScooterId("scooterino124");


        UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);

        assertEquals("scooterino124",booking.getScooterId());



    }

    @Test
    public void UpdateRequestIsNull() {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;

        try {
            jsonNode = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();

        try {
            updateBookingRequest =  mapper.treeToValue(jsonNode, UpdateBookingRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        updateBookingRequest = null;


        LocalDateConverter converter = new LocalDateConverter();
        InstantConverter instantConverter = new InstantConverter();

        Booking booking = new Booking();
        booking.setTripStatus(TripStatus.WAITING_TO_START);
        booking.setUserId("123abc");
        booking.setDate(converter.unconvert("2019-08-25"));
        booking.setStartTime(instantConverter.unconvert("2019-08-30T15:00:36.739Z"));
        booking.setEndTime(instantConverter.unconvert("2019-08-30T16:00:36.739Z"));
        booking.setUserId("abcdgwhwer23");
        booking.setBookingId("myb00kingid");
        booking.setScooterId("scooterino124");



        UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);

        assertEquals("2019-08-25",booking.getDate().toString());



    }

    @Test(expected = NullPointerException.class)
    public void nullTest() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> input = new HashMap<>();

        JsonNode body = mapper.readTree((String) input.get("body"));

    }

}
