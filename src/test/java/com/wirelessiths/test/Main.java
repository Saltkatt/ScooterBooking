package com.wirelessiths.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import com.wirelessiths.dal.trip.Trip;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.ArrayList;
import java.util.List;

public class Main {

//    public static void main(String[] args) {
//        ObjectMapper objectMapper = new ObjectMapper()
//                .configure(
//                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//
//        Dotenv dotenv = Dotenv.load();
//        String tripsUrl = dotenv.get("TRIP_URL");
//        String bookingUrl = dotenv.get("BOOKING_URL");
//        String auth = dotenv.get("AUTH");
//        String scooterId = dotenv.get("SCOOTER_ID");
//        String token = dotenv.get("TOKEN");
//        String tripId = dotenv.get("TRIP_ID");
//
//        GetExample example = new GetExample();
//        List<trip> trips100 = new ArrayList<>();
//
//
//        try {
//           //String result =  example.run(baseUrl + path);
//            String result = example.run(tripsUrl, auth);
//            System.out.println(result);
//
//            ArrayNode trips = (ArrayNode) objectMapper.readTree(result)
//                    .path("trip_overview_list");
//            System.out.println(trips.size());
//            List<trip> trips2 = objectMapper.convertValue(trips, new TypeReference<List<trip>>(){});
//            trips2.forEach(System.out::println);
//
////            trips.forEach(p->{
////                System.out.println(p);
////                trip t = objectMapper.readValue(p, trip.class);
////            });
//           //List<trip> trips200 = objectMapper.treeToValue(trips);
//            //trip t = objectMapper.readValue(result, trip.class);
//            //List<trip> trips = objectMapper.readValue(result, trip.class);
//            //trip[] trips = objectMapper.readValue(result, trip[].class);
//            //System.out.println(b.getEndTime());
//            //System.out.println(t.getEndPosition().getLatitude());
//            //System.out.println(t);
//
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }


}
