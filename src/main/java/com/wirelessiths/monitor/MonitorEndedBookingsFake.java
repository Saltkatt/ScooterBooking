package com.wirelessiths.monitor;

import com.amazonaws.secretsmanager.caching.SecretCache;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.trip.Trip;
import com.wirelessiths.service.HTTPGetService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

public class MonitorEndedBookingsFake {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private HTTPGetService getRequest = new HTTPGetService();

    private Dotenv dotenv = Dotenv.load();
    private String baseUrl = dotenv.get("BASE_URL");
    private String tripEndpoint = dotenv.get("TRIP_ENDPOINT");
    private String vehicleId = dotenv.get("SCOOTER_ID");
    private String authHeader = dotenv.get("AUTH");
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    //complete fake endedBookingsMonitor
    //get trips from today
    //check if they are not cancelled
    //connect trips to bookings
    //save trips to bookings

    //complete real endedBookingsMonitor
    //add credentials to secret manager
    //get secrets in code
    //query p&j for timespan
    //save trips to bookings




    public void doStuff(){
        //cashing secret
        //private final SecretCache cache = new SecretCache();
        //final String secret = cache.getSecretString("");

        //check if ended not cancelled bookings
        //if not log and return
        //else log and check p&j for trips
        //if no trips log and return
        //else log and append trip to booking

        //how query for dates??

        //String secret = getSecret();

        Booking booking = new Booking();
        List<Booking> endedBookings = null;

        try{
            endedBookings = booking.bookingsByEndTime();

        }catch(Exception e) {
            logger.info(e.getMessage());
        }
        if(endedBookings == null || endedBookings.size()  < 1){
            logger.info("No ended bookings");
            return;
        }
        logger.info("number of bookings ended: " + endedBookings.size());

        //String url = String.format("%s/%s%s", baseUrl, vehicleId, tripEndpoint);
        endedBookings.forEach((endedBooking)->{

            String url = String.format("%s/%s%s", baseUrl, endedBooking.getScooterId(), tripEndpoint);
            logger.info("booking ended: " + endedBooking);
            String queryUrl = url + "?startDate=" + endedBooking.getBookingDate();
            try{
                String result = getRequest.run(queryUrl, authHeader);
                ArrayNode trips = (ArrayNode) objectMapper.readTree(result)
                        .path("trip_overview_list");

                if(trips.size() < 1){
                    logger.info("No trips for booking:" + endedBooking);
                }else{
                    List<Trip> newTrips = objectMapper.convertValue(trips, new TypeReference<List<Trip>>(){});
                    newTrips.forEach(trip->{
                        if(trip.getStartTime().isAfter(endedBooking.getStartTime()) && trip.getEndTime().isBefore(endedBooking.getEndTime())){
                            logger.info("appending trip to booking: " + endedBooking);
                            endedBooking.getTrips().add(trip);
                        }
                    });
                    //logger.info("appending " + trips.size() + " new trips to booking: " + endedBooking);
                    //endedBooking.getTrips().addAll(newTrips);
                }

            }catch(IOException e){
                logger.info("IOException: " + e.getMessage());
            }catch(Exception e){
                logger.info("something went wrong: " + e.getMessage());
            }

        });
        //logger.info(secret);
    }

    public static String getSecret() {

        String secretName = "test-secret";
        String region = "us-east-1";

        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.

        String secret, decodedBinarySecret;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InternalServiceErrorException e) {
            // An error occurred on the server side.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidParameterException e) {
            // You provided an invalid value for a parameter.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidRequestException e) {
            // You provided a parameter value that is not valid for the current state of the resource.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (ResourceNotFoundException e) {
            // We can't find the resource that you asked for.
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        }

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
            return secret;

        }
        else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
            return decodedBinarySecret;
        }
        // Your code goes here.
    }
}