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

/**
 * Creates a S3 bucket if it does not already exist.
 */
public class CreateBucket {

    public static void createBucket() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = System.getenv("BUCKET_NAME");

        try{
            if(!s3.doesBucketExistV2(bucketName)){
                s3.createBucket(bucketName);
                System.out.println("Created bucket: " + bucketName);
            }
            else {
                System.out.println("Bucket name already exists!");
            }

        }catch(AmazonS3Exception ex) {
            //logger.error(ex.getMessage());
            ex.getMessage();
        }catch (AmazonServiceException ex){
            System.out.println("AmazonServiceException");
            ex.getMessage();
        }

    }
}
