package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

public class CreateBucket {

 /*   private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String bucketName = "booking-admin-settings";

            if(!s3.doesBucketExistV2(bucketName)) {
                s3.createBucket(bucketName);
            }

            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody()
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (AmazonS3Exception ex) {
            logger.error("Error in creating bucket: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("AmazonS3Exception - missing key: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        }catch (Exception ex){
            logger.error("Error unknown Exception" + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back

            Response responseBody = new Response("Error in creating S3 bucket due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
    }*/

    public static void createBucket() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "booking-admin-settings" + System.currentTimeMillis();

        try{
            if(!s3.doesBucketExistV2(bucketName))
            s3.createBucket(bucketName);
            System.out.println("Created bucket: " + bucketName);
        }catch(AmazonS3Exception ex) {
            //logger.error(ex.getMessage());
            ex.getMessage();
        }catch (AmazonServiceException ex){
            System.out.println("AmazonServiceException");
            ex.getMessage();
        }
    }
}
