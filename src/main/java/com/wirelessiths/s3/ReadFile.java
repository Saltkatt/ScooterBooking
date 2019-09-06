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
import java.util.Map;


public class ReadFile {


   public static Map<String, Integer> readFileInBucket() {
       String keyName = "admin.txt";
       String bucketName = "booking-admin-settings1567688833719";

       final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
       S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
       InputStream objectData = object.getObjectContent();
       //process the objectData stream

       HashMap<String, Integer> setMap = new HashMap<>();
       try {
           BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
           String line;

           while ((line = reader.readLine()) != null) {
               if(line.contains("=")){
                   String[]settings= line.split("=");
                   setMap.put(settings[0], Integer.parseInt(settings[1]));
               }
           }

           //System.out.println(setMap);

         /*  for (Map.Entry<String, Integer> entry : setMap.entrySet()) {
               System.out.println( entry.getKey() + " "+ entry.getValue());
           }*/

           objectData.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return setMap;
   }

}
