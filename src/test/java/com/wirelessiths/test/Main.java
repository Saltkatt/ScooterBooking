package com.wirelessiths.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.wirelessiths.dal.trip.Trip;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        Dotenv dotenv = Dotenv.load();
        String tripsUrl = dotenv.get("TRIP_URL");
        String bookingUrl = dotenv.get("BOOKING_URL");
        String auth = dotenv.get("AUTH");
        String scooterId = dotenv.get("SCOOTER_ID");
        String token = dotenv.get("TOKEN");
        String tripId = dotenv.get("TRIP_ID");

        GetExample example = new GetExample();
        List<Trip> trips100 = new ArrayList<>();


        try {
           //String result =  example.run(baseUrl + path);
            String result = example.run(tripsUrl + "/" + tripId, auth);
            System.out.println(result);

            //JsonNode trips = objectMapper.readTree(result)
//                    .path("trip_overview_list");
//            trips.forEach(p->{
//                System.out.println(p);
//                Trip t = objectMapper.readValue(p, Trip.class);
//            });
           //List<Trip> trips200 = objectMapper.treeToValue(trips);
            Trip t = objectMapper.readValue(result, Trip.class);
            //List<trip> trips = objectMapper.readValue(result, trip.class);
            //Trip[] trips = objectMapper.readValue(result, Trip[].class);
            //System.out.println(b.getEndTime());
            //System.out.println(t.getEndPosition().getLatitude());
            System.out.println(t);

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }


}
