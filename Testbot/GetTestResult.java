package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessamazon.ApiGatewayResponse;

import java.util.*;

public class GetTestResult implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {

            String tableName = "TestBot";
            final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.defaultClient();
            DynamoDB dynamoDB = new DynamoDB(ddb);
            Table table = dynamoDB.getTable(tableName);

            GetItemSpec getItemSpec = new GetItemSpec()
                    .withPrimaryKey("PK", "testbotresult");
            Item item = table.getItem(getItemSpec);

            return ApiGatewayResponse.builder()
                    .setStatusCode(200).setObjectBody(item.toJSON())
                    .build();

        } catch (final Exception exception) {

            exception.printStackTrace();

            return ApiGatewayResponse.builder()
                    .setStatusCode(501)
                    .setObjectBody("Unable to find the attributes: " + exception.toString())
                    .build();
        }
    }

}


