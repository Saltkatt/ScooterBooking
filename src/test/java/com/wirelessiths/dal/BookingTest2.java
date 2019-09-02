package com.wirelessiths.dal;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class BookingTest2 {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table";


    @BeforeClass
    public static void setUpClientAndTable(){
        createClient();
        createTable();
    }


    //@Before//run before test
    public void populateLocalTable(){

    }

    public void populateForValidationTest(){
        Booking booking = new Booking(this.client, this.mapperConfig );

        booking.setScooterId("1");
        booking.setUserId("test-1");
        booking.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        booking.setDate(LocalDate.parse("2019-09-02"));
    }


    @Test
    public void daoCrudTest(){
        Booking booking = new Booking(this.client, this.mapperConfig );

        booking.setScooterId("1");
        booking.setUserId("test-1");
        booking.setStartTime(Instant.parse("2019-09-02T10:00:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T11:00:00.000Z"));
        booking.setDate(LocalDate.parse("2019-09-02"));

        try{
            //create booking
            Booking savedBooking = booking.save(booking);
            //read by bookingId
            assertEquals(savedBooking, booking.get(savedBooking.getBookingId()));
            //delete booking
            assert(booking.delete(savedBooking.getBookingId()));
            //delete deleted booking
            assert(!booking.delete(savedBooking.getBookingId()));
        }catch(Exception e){

            System.out.println(e.getMessage());

        }

    }

//
//    @Test
//    public void validationTestEdgeCases(){
//
//
//    }


    @AfterClass
    public static void deleteTable(){
        Table table = new DynamoDB(client).getTable(tableName);
        try {
            System.out.println("deleting table..");
            table.delete();
            table.waitForDelete();
            System.out.print("table deleted.");

        }
        catch (Exception e) {
            System.err.println("Unable to delete table: ");
            System.err.println(e.getMessage());
        }
    }


    public static void createClient(){
        System.out.println("creating client..");
        client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.US_EAST_1.getName()))
                .build();

        mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
                .build();
        System.out.println("client created.");
    }


    public static void createTable(){

        System.out.println("creating table..");

        //primary key
        List<KeySchemaElement> elements = new ArrayList<>();
        KeySchemaElement hashKey = new KeySchemaElement()
                .withKeyType(KeyType.HASH)
                .withAttributeName("scooterId");

        KeySchemaElement rangeKey = new KeySchemaElement()
                .withKeyType(KeyType.RANGE)
                .withAttributeName("endTime");
        elements.add(hashKey);
        elements.add(rangeKey);


        //global secondary indexes
        List<GlobalSecondaryIndex> globalSecondaryIndexes = new ArrayList<>();

        //userIndex
        ArrayList<KeySchemaElement> userIndexKeySchema = new ArrayList<>();
        userIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("userId")
                .withKeyType(KeyType.HASH));  //Partition key
        userIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("startTime")
                .withKeyType(KeyType.RANGE));  //Sort key

        GlobalSecondaryIndex userIndex = new GlobalSecondaryIndex()
                .withIndexName("userIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 1)
                        .withWriteCapacityUnits((long) 1))
                .withKeySchema(userIndexKeySchema)
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        //bookingIndex
        ArrayList<KeySchemaElement> bookingIndexKeySchema = new ArrayList<>();
        bookingIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("bookingId")
                .withKeyType(KeyType.HASH));  //Partition key
        bookingIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("startTime")
                .withKeyType(KeyType.RANGE));  //Sort key

        GlobalSecondaryIndex bookingIndex = new GlobalSecondaryIndex()
                .withIndexName("bookingIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 1)
                        .withWriteCapacityUnits((long) 1))
                .withKeySchema(bookingIndexKeySchema)
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        //dateIndex
        ArrayList<KeySchemaElement> dateIndexKeySchema = new ArrayList<>();
        dateIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("date")
                .withKeyType(KeyType.HASH));  //Partition key
        dateIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("scooterId")
                .withKeyType(KeyType.RANGE));  //Sort key

        GlobalSecondaryIndex dateIndex = new GlobalSecondaryIndex()
                .withIndexName("dateIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 1)
                        .withWriteCapacityUnits((long) 1))
                .withKeySchema(dateIndexKeySchema)
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));


        globalSecondaryIndexes.add(userIndex);
        globalSecondaryIndexes.add(bookingIndex);
        globalSecondaryIndexes.add(dateIndex);


        //all fields used as keys
        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("scooterId")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("endTime")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("userId")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("bookingId")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("date")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("startTime")
                .withAttributeType(ScalarAttributeType.S));



        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(elements)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L))
                .withGlobalSecondaryIndexes(globalSecondaryIndexes)
                .withAttributeDefinitions(attributeDefinitions);
        client.createTable(createTableRequest);
        System.out.println("table created.");
    }

}
