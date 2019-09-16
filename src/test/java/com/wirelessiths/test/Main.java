package com.wirelessiths.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.wirelessiths.dal.Trip.Trip;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        Dotenv dotenv = Dotenv.load();
        String tripsUrl = dotenv.get("TRIP_URL");
        String bookingUrl = dotenv.get("BOOKING_URL");
        String auth = dotenv.get("AUTH");
        String scooterId = dotenv.get("SCOOTER_ID");
        String token = dotenv.get("TOKEN");
        String tripId = dotenv.get("TRIP_ID");

        GetExample example = new GetExample();
        try {
           //String result =  example.run(baseUrl + path);
            String result = example.run(tripsUrl, auth);
            System.out.println(result);
            //result = result.split(":")[1];
            //Trip t = objectMapper.readValue(result, Trip.class);
            //List<Trip> trips = objectMapper.readValue(result, Trip.class);
            //Trip[] trips = objectMapper.readValue(result, Trip[].class);
            //System.out.println(b.getEndTime());
            //System.out.println(t.getEndPosition().getLatitude());
            //System.out.println(t);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
