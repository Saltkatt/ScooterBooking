package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.trip.Trip;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter implements DynamoDBTypeConverter<String, List<Trip>> {

            //private Logger logger = new LoggerAdapter(LogManager.getLogger(this.getClass()));


    @Override
    public String convert(List<Trip> trips) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        try{
            mapper.writeValue(out, trips);
        }catch(Exception e){
            //logger.info(e.getMessage());
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
            //logger.info(e.getMessage());

            System.out.println(e.getMessage());
        }
        return trips;
    }
}
