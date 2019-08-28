package com.wirelessiths.test.dal;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

public class DynamoDBAdapter {

    private static DynamoDBAdapter db_adapter = null;
    private final AmazonDynamoDB client;
    private DynamoDBMapper mapper;
    private DynamoDB dynamoDB;

    private DynamoDBAdapter() {
        // create the client
        //for cloud client
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build();
        // For local client
        // this.client =  AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1")).build();

        this.dynamoDB = new DynamoDB(this.client);
    }

    public static DynamoDBAdapter getInstance() {
        if (db_adapter == null)
            db_adapter = new DynamoDBAdapter();

        return db_adapter;
    }
    public DynamoDB getDynamoDB() { return this.dynamoDB; }

    public AmazonDynamoDB getDbClient() {
        return this.client;
    }

    public DynamoDBMapper createDbMapper(DynamoDBMapperConfig mapperConfig) {
        // create the mapper with the mapper config
        if (this.client != null)
            mapper = new DynamoDBMapper(this.client, mapperConfig);

        return this.mapper;
    }

}
