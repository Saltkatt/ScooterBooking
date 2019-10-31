package com.wirelessiths;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;


import java.time.Instant;

public class TestBotDatabase {
    private DynamoDB dynamoDB;

    public void persistData(boolean success, Instant timestamp) {
        initDynamoDbClient();

        String DYNAMODB_TABLE_NAME = "TestBot";
        PutItemOutcome dbOperationResult = this.dynamoDB.getTable(DYNAMODB_TABLE_NAME)
                .putItem(new PutItemSpec().withItem(new Item()
                        .withString("PK", "testbotresult")
                        .withString("timestamp", timestamp.toString())
                        .withBoolean("success", success)));
        //dbOperationResult.getPutItemResult().
    }


    private void initDynamoDbClient() {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
        AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1);
        this.dynamoDB = new DynamoDB(client);
    }

}

