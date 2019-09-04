package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;

public class ListBuckets {

    public static void listBuckets() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        List<Bucket> buckets = s3.listBuckets();
        for (Bucket b : buckets) {
            System.out.println("* " + b.getName());
        }
    }

}
