package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class DeleteBucket {

    public static void deleteBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";
        try {
            s3.deleteBucket(bucketName);
        }catch(AmazonServiceException ex){
            System.err.println(ex.getErrorMessage());
        }
    }
}
