package com.wirelessiths.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.wirelessiths.dal.Booking;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();


        Dotenv dotenv = Dotenv.load();
        String tripsUrl = dotenv.get("TRIP_URL");

        String path = "";
        String bookingId = "";
        GetExample example = new GetExample();
        try {
           //String result =  example.run(baseUrl + path + "/" + bookingId);
            String result = example.run(tripsUrl);
            System.out.println(result);
            Booking b = objectMapper.readValue(result, Booking.class);
            System.out.println(b.getEndTime());

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
