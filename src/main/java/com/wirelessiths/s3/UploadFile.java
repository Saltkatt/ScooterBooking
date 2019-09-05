package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.net.URISyntaxException;


public class UploadFile {

    public static void uploadFileToBucket() throws URISyntaxException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = "booking-admin-settings1567688833719";
        String keyName = "admin.txt";
        //final String fileName = "settings.txt";
        String filePath = "C://Users/Elin/Desktop/settings.txt";

        try{
            if(s3.doesBucketExistV2(bucketName))
            s3.putObject(bucketName, keyName, new File(filePath));
            System.out.println("Succes uploading");

        }catch (AmazonServiceException ex){
            ex.getMessage();
            System.exit(1);
        }

    }
}
