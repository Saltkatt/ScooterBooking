package com.wirelessiths.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.UpdateBookingRequest;
import org.junit.Test;

import java.io.IOException;

public class Booking {

    String jsonString = "{\n" +
            "  \"bookingId\":\"b53f67c7-4933-4b4a-92bd-21c873146bfb\",\n" +
            "  \"userId\":\"83396a64-4a39-4c5f-b7e4-8e18b435b41e\",\n" +
            "  \"startTime\":\"2019-08-30T15:00:36.739Z\",\n" +
            "  \"endTime\":\"2019-08-30T16:00:36.739Z\",\n" +
            "  \"date\":\"2019-08-30\"\n" +
            "}";

    //"189abd"

    @Test
    public void testSet() {

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

        System.out.println(updateBookingRequest.toString());

    }

}
