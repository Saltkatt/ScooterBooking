package com.wirelessiths.dal;


import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import org.junit.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BookingQueryTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "query-test-table";



    @BeforeClass
    public static void setUpClientAndTable(){
      //  BookingTest.startLocalDynamoDB();
        createClient();
        createTable();
        populateForQueryTests();
    }

    @AfterClass
    public static void deleteTableAfterTests() {
        deleteTable();
    }


    private static void populateForQueryTests() {

        System.out.println("adding query test cases to table..");
        Booking b1 = new Booking(client, mapperConfig );
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);

        b1.setScooterId("1");
        b1.setUserId("a");
        b1.setStartTime(Instant.parse("2019-09-03T13:20:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-03T13:45:00.000Z"));
        b1.setStartDate(LocalDate.parse("2019-09-03"));

        b2.setScooterId("2");
        b2.setUserId("b");
        b2.setStartTime(Instant.parse("2019-09-04T15:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-04T15:35:00.000Z"));
        b2.setStartDate(LocalDate.parse("2019-09-04"));

        b3.setScooterId("1");
        b3.setUserId("c");
        b3.setStartTime(Instant.parse("2019-09-03T15:10:00.000Z"));
        b3.setEndTime(Instant.parse("2019-09-03T15:35:00.000Z"));
        b3.setStartDate(LocalDate.parse("2019-09-03"));

        b4.setScooterId("4");
        b4.setUserId("c");
        b4.setStartTime(Instant.parse("2019-09-03T13:20:00.000Z"));
        b4.setEndTime(Instant.parse("2019-09-03T13:45:00.000Z"));
        b4.setStartDate(LocalDate.parse("2019-09-03"));

        b5.setScooterId("4");
        b5.setUserId("d");
        b5.setStartTime(Instant.parse("2019-09-04T13:20:00.000Z"));
        b5.setEndTime(Instant.parse("2019-09-04T13:45:00.000Z"));
        b5.setStartDate(LocalDate.parse("2019-09-04"));

        try{
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);

        }catch(Exception e){
            System.out.println("error in getTrips for query test");
            System.out.println("msg: " + e.getMessage());
        }

    }


    //Tests for query methods

    @Test
    public void getByScooterIdNoFilter(){

        System.out.println("getByscooterId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByScooterId("4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(2, list.size());
    }

    @Test
    public void getByUserId(){

        System.out.println("getByUserId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByUserId("c");
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(2, list.size());
    }

    @Test
    public void getByDate(){

        System.out.println("getByUserId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByDate(LocalDate.parse("2019-09-03"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(3, list.size());
    }



    @Test
    public void getByScooterIdAllFilters(){
        System.out.println("getByscooterId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("userId", "c");
        filter.put("bookingDate", "2019-09-03");
        try {
            list = booking.getByScooterIdWithFilter("2",filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(0, list.size());
    }

    @Test
    public void getByScooterIdFilterByDate(){
        System.out.println("getByscooterId filter by bookingDate query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("bookingDate", "2019-09-04");
        try {
            list = booking.getByScooterIdWithFilter("2",filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }

    @Test
    public void getByScooterIdFilterByUserId(){
        System.out.println("getByscooterId filter by userId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("userId", "c");
        try {
            list = booking.getByScooterIdWithFilter("4",filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }

    @Test
    public void getByDateAllFilters(){
        System.out.println("getByDate all filters query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("userId", "c");
        filter.put("scooterId", "4");
        try {
            list = booking.getByDateWithFilter(LocalDate.parse("2019-09-03"),filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());
    }


    @Test
    public void getByDateFilterByUser() {

        System.out.println("getByDate Filter by user query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("userId", "c");
        try {
            list = booking.getByDateWithFilter(LocalDate.parse("2019-09-03"), filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(2, list.size());

    }

    @Test
    public void getByDateFilterByScooter() {

        System.out.println("getByDate Filter by scooter query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("scooterId", "2");
        try {
            list = booking.getByDateWithFilter(LocalDate.parse("2019-09-04"), filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserAllFilters() {
        System.out.println("getByUserId Filter by user query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("bookingDate", "2019-09-03");
        filter.put("scooterId", "1");
        try {
            list = booking.getByUserIdWithFilter("c", filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserFilterByScooterId() {
        System.out.println("getByUserId Filter by scooterId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("scooterId", "1");
        try {
            list = booking.getByUserIdWithFilter("c", filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserFilterByDate() {
        System.out.println("getByUserId Filter by bookingDate query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("bookingDate", "2019-09-03");
        try {
            list = booking.getByUserIdWithFilter("c", filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(2, list.size());

    }



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

        //endTimeIndex
        ArrayList<KeySchemaElement> endTimeIndexKeySchema = new ArrayList<>();
        endTimeIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("bookingDate")
                .withKeyType(KeyType.HASH));  //Partition key
        endTimeIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("endTime")
                .withKeyType(KeyType.RANGE));  //Sort key

        GlobalSecondaryIndex endTimeIndex = new GlobalSecondaryIndex()
                .withIndexName("endTimeIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 1)
                        .withWriteCapacityUnits((long) 1))
                .withKeySchema(endTimeIndexKeySchema)
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));


        globalSecondaryIndexes.add(userIndex);
        globalSecondaryIndexes.add(bookingIndex);
        globalSecondaryIndexes.add(endTimeIndex);

        //local secondary indexes
//        ArrayList<LocalSecondaryIndex> localSecondaryIndexes = new
//                ArrayList<>();
//
//        ArrayList<KeySchemaElement> endTimeIndexKeySchema = new ArrayList<>();
//
//        endTimeIndexKeySchema.add(new KeySchemaElement()
//                .withAttributeName("date")
//                .withKeyType(KeyType.HASH));
//
//        endTimeIndexKeySchema.add(new KeySchemaElement()
//                .withAttributeName("endTime")
//                .withKeyType(KeyType.RANGE));
//
//        LocalSecondaryIndex endTimeIndex = new LocalSecondaryIndex()
//                .withIndexName("endTimeIndex")
//                .withKeySchema(endTimeIndexKeySchema)
//                .withProjection(new Projection().withProjectionType(ProjectionType.KEYS_ONLY));
//
//        localSecondaryIndexes.add(endTimeIndex);


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
                .withAttributeName("bookingDate")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("startTime")
                .withAttributeType(ScalarAttributeType.S));


        try{
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(elements)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(1L)
                            .withWriteCapacityUnits(1L))
                    .withGlobalSecondaryIndexes(globalSecondaryIndexes)
                    //.withLocalSecondaryIndexes(localSecondaryIndexes)
                    .withAttributeDefinitions(attributeDefinitions);
            client.createTable(createTableRequest);
        }catch(Exception e){
            System.out.println("error creating table: " + e.getMessage());

        }
        System.out.println("table created.");


    }

}

