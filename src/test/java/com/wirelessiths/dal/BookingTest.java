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
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

    public class BookingTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table";

    private static int maxDuration = 7200;
    private static int buffer = 300;
    private static int maxBookings = 3;


    @BeforeClass
    public static void setUpClientAndTable(){
        createClient();
        createTable();
        populateForOkValidationTest();
        populateForFailValidationTest();
    }

    //@Before
    public static void populateForOkValidationTest(){
        System.out.println("adding passing test cases to table..");
        Booking b1 = new Booking(client, mapperConfig );
        Booking b2 = new Booking(client, mapperConfig);

        b1.setScooterId("3");
        b1.setUserId("ok-cases");
        b1.setStartTime(Instant.parse("2019-09-02T13:20:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T13:45:00.000Z"));
        b1.setDate(LocalDate.parse("2019-09-02"));

        b2.setScooterId("3");
        b2.setUserId("ok-cases");
        b2.setStartTime(Instant.parse("2019-09-02T15:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-02T15:35:00.000Z"));
        b2.setDate(LocalDate.parse("2019-09-02"));

        try{
            b1.save(b1);
            b2.save(b2);
        }catch(Exception e){
            System.out.println("error in populateForOkValidationTest()100");
            System.out.println("msg: " + e.getMessage());
        }
    }


    @Test
    public void bookingLogicValidationPassTest(){
        Booking testCase = new Booking(client, mapperConfig);

        testCase.setScooterId("3");
        testCase.setUserId("before-and-after");
        testCase.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        testCase.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        testCase.setDate(LocalDate.parse("2019-09-02"));
        //testCase.generateValidationKey();


        try{
            List<Booking> bookings = testCase.validateBooking(testCase, maxDuration, buffer );
            System.out.println("pass test bookings:");
            bookings.forEach(System.out::println);
            System.out.println("pass test bookings.size(): " + bookings.size());
            assert(bookings.size() == 0);
        }catch(Exception e){
            System.out.println("error in bookingLogicValidationPassTest()");

            System.out.println(e.getMessage());
        }
    }

    //@Before
    public static void populateForFailValidationTest(){
        System.out.println("adding fail validation test cases to table..");

        Booking b1 = new Booking(client, mapperConfig );
        Booking b2 = new Booking(client, mapperConfig);
        Booking b3 = new Booking(client, mapperConfig);
        Booking b4 = new Booking(client, mapperConfig);
        Booking b5 = new Booking(client, mapperConfig);
        Booking b6 = new Booking(client, mapperConfig);


        b1.setScooterId("2");
        b1.setUserId("over");
        b1.setBookingId("100");
        b1.setStartTime(Instant.parse("2019-09-02T13:30:00.000Z"));
        b1.setEndTime(Instant.parse("2019-09-02T15:45:00.000Z"));
        b1.setDate(LocalDate.parse("2019-09-02"));

        b2.setScooterId("2");
        b2.setUserId("before-in");
        b2.setStartTime(Instant.parse("2019-09-02T11:10:00.000Z"));
        b2.setEndTime(Instant.parse("2019-09-02T14:35:00.000Z"));
        b2.setDate(LocalDate.parse("2019-09-02"));

        b3.setScooterId("2");
        b3.setUserId("after-out");
        b3.setStartTime(Instant.parse("2019-09-02T14:30:00.000Z"));
        b3.setEndTime(Instant.parse("2019-09-02T15:30:00.000Z"));
        b3.setDate(LocalDate.parse("2019-09-02"));

        b4.setScooterId("2");
        b4.setUserId("between");
        b4.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        b4.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        b4.setDate(LocalDate.parse("2019-09-02"));

        b5.setScooterId("2");
        b5.setUserId("ok-cases");
        b5.setStartTime(Instant.parse("2019-09-02T13:20:00.000Z"));
        b5.setEndTime(Instant.parse("2019-09-02T13:45:00.000Z"));
        b5.setDate(LocalDate.parse("2019-09-02"));

        b6.setScooterId("2");
        b6.setUserId("ok-cases");
        b6.setStartTime(Instant.parse("2019-09-02T15:10:00.000Z"));
        b6.setEndTime(Instant.parse("2019-09-02T15:35:00.000Z"));
        b6.setDate(LocalDate.parse("2019-09-02"));

        try{
            b1.save(b1);
            b2.save(b2);
            b3.save(b3);
            b4.save(b4);
            b5.save(b5);
            b6.save(b6);
        }catch(Exception e){
            System.out.println("error in populateForFailValidationTest()");
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void bookingLogicValidationFailTest(){
        Booking testCase = new Booking(client, mapperConfig);

        testCase.setScooterId("2");
        testCase.setUserId("testCase");
        testCase.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        testCase.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        testCase.setDate(LocalDate.parse("2019-09-02"));

        try{
            List<Booking> bookings = testCase.validateBooking(testCase, maxDuration, buffer);
            System.out.println("fail test bookings.size(): " + bookings.size());

            bookings.forEach(System.out::println);
            assert(bookings.size() == 4);
        }catch(Exception e){
            System.out.println("error in bookingLogicValidationFailTest()");

            System.out.println(e.getMessage());
        }
    }


    @Test
    public void daoCrudTest(){
        Booking booking = new Booking(client, mapperConfig );

        booking.setScooterId("1");
        booking.setUserId("test-1");
        booking.setStartTime(Instant.parse("2019-09-02T14:00:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T15:00:00.000Z"));
        booking.setDate(LocalDate.parse("2019-09-02"));

        try{
            //create booking
            Booking savedBooking = booking.save(booking);
            //read by bookingId
            assertEquals(savedBooking, booking.get(savedBooking.getBookingId()));
            //update
            String newUserId = "test-2";
            booking.setUserId(newUserId);
            booking.update(booking);
            assertEquals(booking.getUserId(), newUserId);

            //delete booking
            assert(booking.delete(savedBooking.getBookingId()));
            //delete deleted booking
            assert(!booking.delete(savedBooking.getBookingId()));
        }catch(Exception e){

            System.out.println(e.getMessage());

        }

    }

    //Tests for query methods

    @Test
    public void getByScooterIdReturnsScootersByThatId(){
        System.out.println("getByscooterId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
           list = booking.getByScooterId("3");
        } catch (IOException e) {
            e.printStackTrace();
        }
       list.forEach(System.out::println);
       assertEquals(2, list.size());
    }

    @Test
    public void getByScooterIdReturnsSizeZeroWhenNoMatching(){
        System.out.println("getByscooterId query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByScooterId("987");
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(0, list.size());
    }

        @Test
        public void getByScooterIdAllFilters(){
            System.out.println("getByscooterId query: ");
            Booking booking = new Booking(client, mapperConfig );
            List<Booking> list = new ArrayList<>();
            Map<String, String> filter = new HashMap<>();
            filter.put("userId", "before-in");
            filter.put("date", "2019-09-03");
            try {
                list = booking.getByScooterIdWithFilter("2",filter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.forEach(System.out::println);
            assertEquals(0, list.size());
        }

    @Test
    public void getByDateGetsAll(){
        System.out.println("getByDate query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByDate(LocalDate.parse("2019-09-02"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(8, list.size());
    }

    @Test
    public void getByDateSizeZeroWhenNoMatchingDate(){
        System.out.println("getByDate query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        try {
            list = booking.getByDate(LocalDate.parse("2019-09-03"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(0, list.size());
    }

    @Test
    public void getByDateFilterByUser() {

        System.out.println("getByDate Filter by user query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("userId", "before-in");
        try {
            list = booking.getByDateWithFilter(LocalDate.parse("2019-09-02"), filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(1, list.size());

    }

    @Test
    public void getByUserIdWithFilterTwoReturns() {
        System.out.println("getByUserId Filter by user query: ");
        Booking booking = new Booking(client, mapperConfig );
        List<Booking> list = new ArrayList<>();
        Map<String, String> filter = new HashMap<>();
        filter.put("date", "2019-09-02");
        filter.put("scooterId", "2");
        try {
            list = booking.getByUserIdWithFilter("ok-cases", filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(System.out::println);
        assertEquals(2, list.size());

    }

        @Test
        public void getByUserIdWithFilterNoReturns() {
            System.out.println("getByUserId Filter by user query: ");
            Booking booking = new Booking(client, mapperConfig );
            List<Booking> list = new ArrayList<>();
            Map<String, String> filter = new HashMap<>();
            filter.put("date", "2019-09-03");
            filter.put("scooterId", "3");
            try {
                list = booking.getByUserIdWithFilter("ok-cases", filter);
            } catch (IOException e) {
                e.printStackTrace();
            }
            list.forEach(System.out::println);
            assertEquals(0, list.size());

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
