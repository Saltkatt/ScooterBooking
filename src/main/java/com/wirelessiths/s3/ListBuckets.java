package com.wirelessiths.s3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListBuckets {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            List<Bucket> buckets = s3.listBuckets();
            for (Bucket b : buckets) {
                //System.out.println("* " + b.getName());
            }

            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody()
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch (Exception ex) {
            logger.error("Error unknown Exception" + ex);
            logger.error(ex.getMessage());

            // send the error response back
            Response responseBody = new Response("Error in listing buckets due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }


    }

   /* public static void listBuckets() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }*/

}
