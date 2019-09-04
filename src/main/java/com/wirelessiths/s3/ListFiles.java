package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

public class ListFiles {
    static String bucketName = "settings";

    public static void listFilesInBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        ObjectListing olist = s3.listObjects(bucketName);
        List<S3ObjectSummary> objects = olist.getObjectSummaries();
        for(S3ObjectSummary os: objects) {
            System.out.println("* " + os.getKey());
        }

    }
}
