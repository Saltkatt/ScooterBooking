package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadFile {

    public static void readFileInBucket() {
        String keyName = "admin.txt";
        String bucketName = "settings";

        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
        InputStream objectData = object.getObjectContent();
        //process the objectData stream

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
            String line;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
            objectData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
