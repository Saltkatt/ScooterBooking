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

    public static void deleteFileInBucket() {

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = System.getenv("BUCKET_NAME");
        String objectKey = "admin.txt";

        try{
            if(s3.doesBucketExistV2(bucketName)){
                s3.deleteObject(bucketName, objectKey);
                System.out.println("Deleted admin.txt");
            }

        }catch(AmazonServiceException ex) {
            System.err.println(ex.getErrorMessage());
            System.exit(1);
        }

    }
}
