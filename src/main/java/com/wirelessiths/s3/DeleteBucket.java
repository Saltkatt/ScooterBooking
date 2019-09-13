package com.wirelessiths.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.exception.CouldNotDeleteBookingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class DeleteBucket {

    public static void deleteBucket() {
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        //String bucketName = System.getenv("BUCKET_NAME");
        String bucketName = "booking-admin-settings";
        try {
            s3.deleteBucket(bucketName);
        }catch(AmazonServiceException ex){
            System.err.println(ex.getErrorMessage());
        }
    }
}
