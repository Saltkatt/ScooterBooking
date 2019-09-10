package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class ReadFile {

    static final Logger logger = LogManager.getLogger(ReadFile.class);


   public static Map<String, Integer> readFileInBucket() throws IOException, Exception {
       String keyName = "admin.txt";
       String bucketName = System.getenv("BUCKET_NAME");

        logger.info("z");
       final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
       logger.info("x");
       S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
       logger.info("v");
       InputStream objectData = object.getObjectContent();
       //process the objectData stream
       logger.info("zz");
       HashMap<String, Integer> setMap = new HashMap<>();

           BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
       logger.info("xx");
           String line;

           while ((line = reader.readLine()) != null) {
               if(line.contains("=")){
                   logger.info("yy");
                   String[]settings= line.split("=");
                   setMap.put(settings[0], Integer.parseInt(settings[1]));
               }
           }

           //System.out.println(setMap);

         /*  for (Map.Entry<String, Integer> entry : setMap.entrySet()) {
               System.out.println( entry.getKey() + " "+ entry.getValue());
           }*/

           objectData.close();

       return setMap;
   }

}
