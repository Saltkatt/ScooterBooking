package com.wirelessiths.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.wirelessiths.dal.Booking;

public class Main {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        String path = "";
        String bookingId = "";
        String tripsUrl = "";
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
