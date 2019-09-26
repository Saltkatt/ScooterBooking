package com.wirelessiths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.*;
import org.junit.Test;

import java.io.IOException;

import static com.wirelessiths.handler.UpdateBookingHandler.setBookingProperties;
import static org.junit.Assert.*;

public class UpdateBookingHandlerTest {


    String jsonString = "{  \n" +
            "   \"scooterId\":\"876rty\",\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\",\n" +
            "   \"date\":\"2019-08-30\"\n" +
            "}";
    String jsonStringNoDate = "{  \n" +
            "   \"scooterId\":\"189abd\",\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\"\n" +
            "}";

    String nullJsonString = "{  \n" +
            "   \"scooterId\":null,\n" +
            "   \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "   \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "   \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "   \"endTime\":\"2019-08-30T16:00:36.739Z\"\n" +
            "}";


    @Test
    public void testBooking() {

        boolean isNew = false;
        Booking newBooking = new Booking();
        UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();
        Booking booking = new Booking();
        ObjectMapper mapper = new ObjectMapper();
        String state = "";

        LocalDateConverter converter = new LocalDateConverter();
        InstantConverter instantConverter = new InstantConverter();

        booking.setUserId("123abc");
        booking.setStartDate(converter.unconvert("2019-08-25"));
        booking.setStartTime(instantConverter.unconvert("2019-08-30T15:00:36.739Z"));
        booking.setEndTime(instantConverter.unconvert("2019-08-30T16:00:36.739Z"));
        booking.setUserId("abcdgwhwer23");
        booking.setBookingId("myb00kingid");
        booking.setScooterId("scooterino124");

        JsonNode body = null;

        try {
            body = mapper.readTree(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }



        try {
            updateBookingRequest = mapper.treeToValue(body, UpdateBookingRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        if (body.has("endTime") || body.has("scooterId")) {

            isNew = true;
            state = "a";

            newBooking = booking;
        }

        if (isNew) {

            if (newBooking != null) {

                newBooking = com.wirelessiths.handler.UpdateBookingHandler.setBookingProperties(updateBookingRequest, newBooking);
                state = "b";
            }
        } else {

            booking = setBookingProperties(updateBookingRequest, booking);
            state = "c";
        }

        assertEquals("b", state);
        assertEquals("876rty", newBooking.getScooterId());



    }


}
