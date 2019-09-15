package com.wirelessiths.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wirelessiths.dal.Booking;

import java.io.IOException;

public class Main {

    //input/requestContext/authorizer/claims/sub
    //header authorizer: { clamims: {sub: "id"} }

    //read up on http and payloads specifically
    //understand http response from java code perspective
    //trigger lambda from cloudwatch

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        String url = "http://www.google.com";
        String baseUrl = "https://nkybxxmihd.execute-api.us-east-1.amazonaws.com/dev";
        String path = "/bookings";
        String bookingId = "b97aea7a-fb81-44f9-a859-6a0604dcfb76";
        GetExample example = new GetExample();
        try {
           String result =  example.run(baseUrl + path + "/" + bookingId);
            System.out.println(result);
            Booking b = objectMapper.readValue(result, Booking.class);
            System.out.println(b.getEndTime());

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
