package com.wirelessiths.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static com.amazonaws.services.s3.AmazonS3ClientBuilder.*;

/**
 * Reads the file in the S3 Bucket.
 */
public class ReadFile {

    /**
     * Reads the file in the S3 bucket and transfers the information to a HashMap.
     * @return HashMap s3Content
     */
    public static HashMap<String, Integer> readFileInBucket() throws IOException {
        String keyName = "admin.txt";
        String bucketName = System.getenv("BUCKET_NAME");
        //String bucketName = "carl-bucket-29";

        final AmazonS3 s3 = defaultClient();
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
        InputStream objectData = object.getObjectContent();
        //process the objectData stream

        HashMap<String, String> setMap = new HashMap<>();
        HashMap<String, Integer> s3Content = new HashMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains("=")) {
                String[] settings = line.split("=");
                //setMap.put(settings[0], Integer.parseInt(settings[1]));
                setMap.put(settings[0], settings[1]);
            }
        }
        for (Map.Entry<String, String> entry : setMap.entrySet()) {
            //System.out.println( entry.getKey() + " "+ entry.getValue());
            String key = entry.getKey();
            String value = entry.getValue();

            // if string value contains * split the string and place in array numbers.
            if (value.contains("*")) {
                String[] numbers = value.split("\\*");
                int sum = 1;
                // loop through array parse string to integers and multiply.
                for (int i = 0; i < numbers.length; i++) {
                    sum = sum * Integer.parseInt(numbers[i]);
                }
                // put the String key and the int sum in hashmap: s3Content.
                s3Content.put(key, sum);

            } else {
                // if string value does not contain * , parse value into a new int oneValue.
                int oneValue = Integer.parseInt(value);
                // put the String key and the int oneValue in hashmap: s3Content.
                s3Content.put(key, oneValue);
            }
        }
        objectData.close();

        return s3Content;
    }

}
