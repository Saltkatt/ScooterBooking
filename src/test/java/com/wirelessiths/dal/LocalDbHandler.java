package com.wirelessiths.dal;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.ArrayList;
import java.util.List;

public class LocalDbHandler {

    public static AmazonDynamoDB createClient(){
        System.out.println("creating client..");
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.US_EAST_1.getName()))
                .build();
    }
    public static DynamoDBMapperConfig createMapperConfig(String tableName){
        System.out.println("creating mapperConfig..");
        return DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
                .build();
    }

    public static void deleteTable(String tableName, AmazonDynamoDB client){
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



    public static void createTable(String tableName, AmazonDynamoDB client){

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
                .withAttributeName("endDate")
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

        ArrayList<KeySchemaElement> startTimeIndexKeySchema = new ArrayList<>();
        startTimeIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("startDate")
                .withKeyType(KeyType.HASH));  //Partition key
        startTimeIndexKeySchema.add(new KeySchemaElement()
                .withAttributeName("startTime")
                .withKeyType(KeyType.RANGE));  //Sort key

        GlobalSecondaryIndex startTimeIndex = new GlobalSecondaryIndex()
                .withIndexName("startTimeIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 1)
                        .withWriteCapacityUnits((long) 1))
                .withKeySchema(startTimeIndexKeySchema)
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));//Todo: change to only include bookingStatus


        globalSecondaryIndexes.add(userIndex);
        globalSecondaryIndexes.add(bookingIndex);
        globalSecondaryIndexes.add(endTimeIndex);
        globalSecondaryIndexes.add(startTimeIndex);

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
                .withAttributeName("startDate")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("startTime")
                .withAttributeType(ScalarAttributeType.S));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("endDate")
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
