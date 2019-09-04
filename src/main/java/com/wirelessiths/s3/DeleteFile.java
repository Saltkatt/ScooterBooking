package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class DeleteFile {

    public static void deleteFileInBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";
        String objectKey = "helloWorld.txt";

        try{
            s3.deleteObject(bucketName, objectKey);
        }catch(AmazonServiceException ex) {
            System.err.println(ex.getErrorMessage());
            System.exit(1);
        }
    }
}
