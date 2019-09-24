package com.wirelessiths.monitor;

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
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class MonitorEndedBookings {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private static Dotenv dotenv = Dotenv.load();
    //private String tripEndpoint = dotenv.get("TRIP_ENDPOINT");

    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    public void lambdaHandler(){
        //cashing secret
        //private final SecretCache cache = new SecretCache();
        //final String secret = cache.getSecretString("");

        //check if ended bookings
        //if not log and return
        //else log and check p&j for trips
        //if no trips log and return
        //else log and append trip to booking

        //how query for dates??

        //String secret = getSecret();

        String clientSecret = getSecret("us-east-1", "client_secret");
        String audience = dotenv.get("AUDIENCE");
        String actor = dotenv.get("ACTOR");
        String authUrl = dotenv.get("AUTH_URL");
        String pjUrl = dotenv.get("PJ_URL");

        List<Booking> endedBookings = getEndedBookings();
        if(endedBookings == null || endedBookings.isEmpty()){
            logger.info("No ended bookings");
            return;
        }
        logger.info("number of bookings ended: " + endedBookings.size());

        Map<String, String> auth = getAuth(audience, actor, clientSecret, authUrl);
        String accessToken = auth.get("access_token");

        endedBookings.forEach((endedBooking)->{

            String vehicleId = endedBooking.getScooterId();
            try{
                String response = getTrips(accessToken, vehicleId, pjUrl);
                ArrayNode trips = (ArrayNode) mapper.readTree(response)
                        .path("trip_overview_list");
                List<Trip> newTrips = mapper.convertValue(trips, new TypeReference<List<Trip>>(){});
                if(trips.size() == 0){
                    logger.info("No trips for booking:" + endedBooking);
                    return;
                }
                logger.info("number of trips found: " + trips.size());
                endedBooking.getTrips().addAll(newTrips);
                endedBooking.save(endedBooking);
                logger.info("saving updated booking");

            }catch(IOException e) {
                logger.info(e.getMessage());
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        });
    }

    private List<Booking> getEndedBookings(){
        Booking booking = new Booking();
        List<Booking> endedBookings = null;

        try{
            endedBookings = booking.bookingsByEndTime();

        }catch(Exception e) {
            logger.info(e.getMessage());
        }
        logger.info("number of bookings ended: " + endedBookings.size());
        return endedBookings;
    }

    private String getTrips(String accessToken, String vehicleId, String url) throws IOException {

        if (accessToken == null) {
           logger.info("no token");
            return null;
        }

        String fullUrl = url + "/vehicles/" + vehicleId + "/trips";
        //Todo: add query params for startDate and endDate
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", accessToken)
                .build();
        //Response response = null;
        Response response = client.newCall(request).execute();
        //return mapper.readValue(response.body().string(), new TypeReference<Map<String, String>>() {});
        return response.body().string();
    }


    private Map<String, String> getAuth(String audience, String actor, String clientSecret, String authUrl){

        ObjectMapper mapper = new ObjectMapper();

        String stringBody = String.format("{\"audience\":\"%s\", \"grant_type\":\"client_credentials\"," +
                        " \"client_id\":\"%s\",\"client_secret\":\"%s\"}",
                audience, actor, clientSecret);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, stringBody);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(authUrl)
                .addHeader("Content-type", "application/json")
                .post(body)
                .build();
        Response response = null;
        try{
            response = client.newCall(request).execute();
            return mapper.readValue(response.body().string(), new TypeReference<Map<String, String>>() {});

        }catch(Exception e){
            logger.info("error: " + e.getMessage());
        }
        return null;
    }


    private static String getSecret(String region, String secretName) {

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

