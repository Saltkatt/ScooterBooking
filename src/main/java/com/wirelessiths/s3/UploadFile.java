package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
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

public class UploadFile {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String bucketName = "booking-admin-settings";
            String keyName = "admin.txt";
            final String fileName = "admin-settings.txt";
            File file = new File(S3BucketHandler.class.getResource(fileName).toURI());

            if(s3.doesBucketExistV2(bucketName)) {
                s3.putObject(bucketName, keyName, file);
            }

            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody()
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (AmazonS3Exception ex) {
            logger.error("Error in uploading file to bucket: " + ex);
            logger.error(ex.getMessage());

            // send the error response back
            Response responseBody = new Response("AmazonS3Exception - missing key: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        }catch (AmazonServiceException ex){
            logger.error("Error due unknown service error" + ex);
            logger.error(ex.getMessage());

            // send the error response back
            Response responseBody = new Response("Request correctly transmitted, service could not process: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        } catch (Exception ex){
            logger.error("Error unknown Exception" + ex);
            logger.error(ex.getMessage());

            // send the error response back
            Response responseBody = new Response("Error in uploading file to bucket due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
    }


   /* public static void uploadFileToBucket() throws URISyntaxException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "booking-admin-settings";
        String keyName = "admin.txt";
        final String fileName = "admin-settings.txt";
        File file = new File(S3BucketHandler.class.getResource(fileName).toURI());

        try{
            if(s3.doesBucketExistV2(bucketName))
            s3.putObject(bucketName, keyName, file);

        }catch (AmazonServiceException ex){
            ex.getMessage();
            System.exit(1);
        }

    }*/
}
