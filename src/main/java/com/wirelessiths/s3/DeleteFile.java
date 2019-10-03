package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/**
 * Deletes the file (admin.txt) in the S3 Bucket.
 */
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
