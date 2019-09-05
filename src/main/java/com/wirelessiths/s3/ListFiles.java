package com.wirelessiths.s3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListFiles {


    static String bucketName = "settings";

    public static void listFilesInBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        ObjectListing olist = s3.listObjects(bucketName);
        List<S3ObjectSummary> objects = olist.getObjectSummaries();
        for(S3ObjectSummary os: objects) {
            System.out.println("* " + os.getKey());
        }

    }
}
