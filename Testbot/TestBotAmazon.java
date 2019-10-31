package com.wirelessamazon;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.TestBot;
import com.wirelessiths.TestBotDatabase;

import java.time.Instant;
import java.util.Map;

public class TestBotAmazon implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


        try {
            String userName = System.getenv("userName");
            String password = System.getenv("password");

            TestBot testBot = new TestBot();
            boolean result = testBot.performTesting(userName, password);
            Instant timestamp = Instant.now();
            TestBotDatabase database = new TestBotDatabase();
            database.persistData(result, timestamp);


            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("The Testbot ran smoothly. ")
                    .build();

        } catch (final Exception exception) {

            exception.printStackTrace();

            return ApiGatewayResponse.builder()
                    .setStatusCode(501)
                    .setObjectBody(exception.toString())
                    .build();
        }
    }

}
