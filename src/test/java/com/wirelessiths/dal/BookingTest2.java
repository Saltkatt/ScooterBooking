package com.wirelessiths.dal;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.junit.Before;
import org.junit.Test;

public class BookingTest2 {

    private Booking booking;
    private AmazonDynamoDB client;
    private DynamoDBMapperConfig mapperConfig;

    @Before
    public void createClient(){
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.US_EAST_1.getName()))
                .build();

        this.mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride("test-table"))
                .build();
    }

    @Before
    public void createDao(){
        this.booking = new Booking(this.client, this.mapperConfig );
    }



    @Test
    public void validationTest(){

    }

}
