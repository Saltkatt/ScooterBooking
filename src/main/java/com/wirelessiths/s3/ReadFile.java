package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


public class ReadFile {


   public static void readFileInBucket() {
       String keyName = "admin.txt";
       String bucketName = "booking-admin-settings1567688833719";

       final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
       S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
       InputStream objectData = object.getObjectContent();
       //process the objectData stream

       try {
           BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
           String line;
           HashMap<String, String> setMap = new HashMap<>();

           //parse String to int

           while ((line = reader.readLine()) != null) {
               String[]settings= line.split("=");
               setMap.put(settings[0], settings[1]);
               System.out.println(setMap);
           }

           objectData.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

}
