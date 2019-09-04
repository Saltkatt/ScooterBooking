package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateBucket {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public static void createBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";

        try{
            s3.createBucket(bucketName);
        }catch(AmazonS3Exception ex) {
            //logger.error(ex.getMessage());
            ex.getMessage();

        }
    }
}
