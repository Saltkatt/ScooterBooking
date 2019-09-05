package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.exception.CouldNotDeleteBookingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class DeleteBucket {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("bucketName");
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

            String bucketName = pathParameters.get("booking-admin-settings");


            // send the response back
            if (s3.doesBucketExistV2(bucketName)) {
                s3.deleteBucket(bucketName);

                return ApiGatewayResponse.builder()
                        .setStatusCode(204)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Bucket: '" + bucketName + "' not found.")
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
        } catch (IllegalStateException ex) {
            logger.error("Error in deleting booking: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Error in deleting bucket, state is null: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch (Exception ex) {
            logger.error("Error in deleting bucket: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Unknown error in deleting bucket: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }

    /*public static void deleteBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";
        try {
            s3.deleteBucket(bucketName);
        }catch(AmazonServiceException ex){
            System.err.println(ex.getErrorMessage());
        }
    }*/
}
