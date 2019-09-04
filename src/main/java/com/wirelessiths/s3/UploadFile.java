package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.net.URISyntaxException;

public class UploadFile {

    public static void uploadFileToBucket() throws URISyntaxException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "settings";
        String keyName = "admin.txt";
        final String fileName = "admin-settings.txt";
        File file = new File(S3BucketHandler.class.getResource(fileName).toURI());

        try{
            s3.putObject(bucketName, keyName, file);

        }catch (AmazonServiceException ex){
            ex.getMessage();
            System.exit(1);
        }


    }
}
