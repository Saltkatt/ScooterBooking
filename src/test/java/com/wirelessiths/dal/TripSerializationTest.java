package com.wirelessiths.dal;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wirelessiths.dal.trip.Trip;
import com.wirelessiths.service.HTTPGetService;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

public class TripSerializationTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table-serialization";

    private static HTTPGetService getRequest = new HTTPGetService();

    private static Dotenv dotenv = Dotenv.load();
    private static String baseUrl = dotenv.get("BASE_URL");
    private static String tripEndpoint = dotenv.get("TRIP_ENDPOINT");
    private static String authHeader = dotenv.get("AUTH");
    private static String vehicleId = dotenv.get("SCOOTER_ID");
    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);




    @BeforeClass
    public static void create(){
      createClient();
      createTable();
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

    public static List<Trip> getTrips(){


        String url = String.format("%s/%s%s", baseUrl, vehicleId, tripEndpoint);
        String queryUrl = url + "?startDate=" + vehicleId;

        try{
            String result = getRequest.run(queryUrl, authHeader);
            ArrayNode trips = (ArrayNode) objectMapper.readTree(result)
                    .path("trip_overview_list");

            return objectMapper.convertValue(trips, new TypeReference<List<Trip>>(){});

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Test
    public void serializeTripTest(){
        Booking booking = new Booking(client, mapperConfig);
        Booking booking3 = new Booking(client, mapperConfig);


        booking.setScooterId("100");
        booking.setUserId("test-append-trip");
        booking.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        booking.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        booking.setBookingDate(LocalDate.parse("2019-09-02"));
        booking.setBookingStatus(BookingStatus.VALID);

        booking3.setScooterId("3");
        booking3.setUserId("test-append-trip3");
        booking3.setStartTime(Instant.parse("2019-09-02T14:10:00.000Z"));
        booking3.setEndTime(Instant.parse("2019-09-02T14:40:00.000Z"));
        booking3.setBookingDate(LocalDate.parse("2019-09-02"));
        booking3.setBookingStatus(BookingStatus.VALID);

        List<Trip> newTrips = getTrips();
        assert(newTrips != null && !newTrips.isEmpty());

        try{
            booking3.getTrips().add(newTrips.get(0));
            System.out.println("3,1: " + booking3);
            booking3.save(booking3);

            booking3.get(booking3.getBookingId());
            newTrips.forEach(trip->booking.getTrips().add(trip));
            booking.save(booking);
            System.out.println("booking saved: " + booking);
            System.out.println("3,2: " + booking3);

            System.out.println("booking id: " + booking.getBookingId());
            Booking booking2 = booking.get(booking.getBookingId());
            assert(!booking2.getTrips().isEmpty());
            System.out.println("booking2: " + booking2);
        }catch(Exception e){
            System.out.println(e.getMessage());
            fail();
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
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));//Todo: change to only include bookingStatus


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
