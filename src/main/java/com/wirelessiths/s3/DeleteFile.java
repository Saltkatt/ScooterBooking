package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeleteFile {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'pathParameters' from input, is it needed for S3??
            Map<String,String> pathParameters =  (Map<String,String>)input.get("bucketName");
            final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

            String bucketName = "booking-admin-settings";
            String objectKey = "admin.txt";

            // send the response back
            if (s3.doesBucketExistV2(bucketName)) {
                s3.deleteObject(bucketName, objectKey);

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
            logger.error("Error in deleting file in bucket: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Error in deleting file in bucket, state is null: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch (Exception ex) {
            logger.error("Error in deleting file in bucket: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Unknown error in deleting file in bucket: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }

        /*public static void deleteFileInBucket() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";
        String objectKey = "helloWorld.txt";

        try{
            if(s3.doesBucketExistV2(bucketName)){
                s3.deleteObject(bucketName, objectKey);
            }

        }catch(AmazonServiceException ex) {
            System.err.println(ex.getErrorMessage());
            System.exit(1);
        }

        }*/
}
