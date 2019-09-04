package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.wirelessiths.ApiGatewayResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class S3BucketHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


    }

    public static void createAndPopulateBucket() throws IOException {
        Regions clientRegion = Regions.DEFAULT_REGION;
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(Credentials.access_key_id, Credentials.secret_access_key);
        String newBucketName = "settings" + System.currentTimeMillis();
        String bucketName = newBucketName;

        try{
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();


            if(!s3Client.doesBucketExistV2(bucketName)) {
                s3Client.createBucket(new CreateBucketRequest(bucketName));
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location:  " + bucketLocation);
                s3Client.createBucket(newBucketName);

                // Populate bucket with a text file.
                final String fileName = "sometext.txt";

                File file = new File(S3BucketHandler.class.getResource(fileName).toURI());

                {
                    PutObjectRequest putRequest1 = new PutObjectRequest(newBucketName, fileName + "." + System.currentTimeMillis(), file);
                    PutObjectResult response1 = s3Client.putObject(putRequest1);
                    System.out.println("Uploaded object encryption status is " +
                            response1.getSSEAlgorithm());
                }
            }
        }catch (AmazonServiceException ex) {
            ex.printStackTrace();
            ex.getMessage();
        }catch (SdkClientException ex) {
            ex.printStackTrace();
            ex.getMessage();
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
            ex.getMessage();
        }

    }


    public static void deleteBucket() {


    }

    public static void listAllBuckets() {


    }


}
