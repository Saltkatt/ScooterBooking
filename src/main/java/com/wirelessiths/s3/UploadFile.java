package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;


public class UploadFile {

    public static void uploadFileToBucket() throws URISyntaxException {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String bucketName = System.getenv("BUCKET_NAME");
        String keyName = "admin.txt";
        final String fileName = "settings.txt";

        File filePath = UploadFile.getFileFromResources("settings.txt");

        try{
            if(s3.doesBucketExistV2(bucketName)) {
                s3.putObject(bucketName, keyName, new File(String.valueOf(filePath)));
                System.out.println("Success uploading");
            }
            else{
                System.out.println("Bucket does not exist.");
            }

        }catch (AmazonServiceException ex){
            ex.getMessage();
            System.exit(1);
        }
        catch (NullPointerException ex) {
            ex.getMessage();
            System.out.println("Nullpointer exception");
            System.exit(1);
        }

    }

    /**
     * Accesses files in resources folder.
     * @param fileName
     * @return new File
     */
    private static File getFileFromResources(String fileName) {

        ClassLoader classLoader = UploadFile.class.getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if(resource == null) {
            throw new IllegalArgumentException("file not found!");
        }else{
            return new File(resource.getFile());
        }
    }
}
