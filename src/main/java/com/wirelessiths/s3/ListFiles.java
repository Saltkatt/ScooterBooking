package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;

import static com.amazonaws.services.s3.AmazonS3ClientBuilder.*;

/**
 * Lists all files in the S3 Bucket.
 */
public class ListFiles {

    public static void listFilesInBucket() {
        String bucketName = System.getenv("BUCKET_NAME");
        //String bucketName = "carl-bucket-29";
        final AmazonS3 s3 = defaultClient();
        ObjectListing olist = s3.listObjects(bucketName);
        List<S3ObjectSummary> objects = olist.getObjectSummaries();
        for(S3ObjectSummary os: objects) {
            System.out.println("* " + os.getKey());
        }

    }
}
