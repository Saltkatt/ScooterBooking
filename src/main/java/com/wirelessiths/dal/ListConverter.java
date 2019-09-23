package com.wirelessiths.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wirelessiths.dal.trip.Trip;
import org.apache.logging.log4j.LogManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ListConverter implements DynamoDBTypeConverter<String, List<Trip>> {

    @Override
    public String convert(List<Trip> trips) {
        System.out.println("convert");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final ObjectMapper mapper = new ObjectMapper();

        try{

            mapper.writeValue(out, trips);
        }catch(Exception e){

        }

        final byte[] data = out.toByteArray();
        //this.logger.info("convert list 6");
        System.out.println("exit convert");
        return new String(data);
    }

    @Override
    public List<Trip> unconvert(String s) {
        System.out.println("unconvert");
        System.out.println("s: " + s);

        ObjectMapper mapper = new ObjectMapper();


        try{
            ArrayNode tripsNode = (ArrayNode) mapper.readTree(s);
            System.out.println("node: " + tripsNode.size());

            List<Trip> trips = mapper.convertValue(tripsNode, new TypeReference<List<Trip>>(){});
            System.out.println("trips: " + trips.size());
            trips.forEach(t->System.out.println(t));

            return mapper.convertValue(tripsNode, new TypeReference<List<Trip>>(){});


        }catch(Exception e){
            System.out.println(e.getMessage());

        }
        return null;
    }
}
