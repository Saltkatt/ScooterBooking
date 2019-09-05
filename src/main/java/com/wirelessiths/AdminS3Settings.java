package com.wirelessiths;

import com.sun.xml.internal.ws.api.config.management.policy.ManagementAssertion;
import com.wirelessiths.s3.S3BucketHandler;

import java.io.File;

public class AdminS3Settings {

    // Get object in bucket using ReadFile
    //May need two files in bucket called buffer and maxDuration with one value in each.

    Settings settings = new Settings();


    public static void bufferValue() {
        String bucketName = "settings";
        String objectKey = "helloWorld.txt";
        //DeleteFile with old value

        //UploadFile with new value
        String bucketName = "settings";
        String keyName = "admin.txt";
        final String fileName = "admin-settings.txt";
        File file = new File(S3BucketHandler.class.getResource(fileName).toURI());


    }

    public static void maxDuration() {
        //DeleteFile with old value
        //UploadFile with new value



    }
}
