package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.trip.Trip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter implements DynamoDBTypeConverter<String, List<Trip>> {


    @Override
    public String convert(List<Trip> trips) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.writeValue(out, trips);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        final byte[] data = out.toByteArray();
        return new String(data);
    }

    @Override
    public List<Trip> unconvert(String s) {
        ObjectMapper mapper = new ObjectMapper();

        List<Trip> trips = new ArrayList<>();
        try{
            trips = Arrays.asList(mapper.readValue(s, Trip[].class));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return trips;
    }
}
