package com.wirelessiths.monitor;

import com.amazonaws.regions.Regions;
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

//Todo: add cloudwatch rules to sam template
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

        String clientSecret = getSecret(Regions.EU_WEST_1.toString(), "client_secret");
        String audience = dotenv.get("AUDIENCE");
        String actor = dotenv.get("ACTOR");
        String authUrl = dotenv.get("AUTH_URL");
        String pjUrl = dotenv.get("PJ_URL");

        Booking booking = new Booking();
        List<Booking> endedBookings = booking.bookingsByEndTime();
        if(endedBookings.isEmpty()){
            logger.info("No ended bookings");
            return;
        }
        logger.info("number of bookings ended: {}", endedBookings.size());

        try{
            Map<String, String> auth = getAuth(audience, actor, clientSecret, authUrl);
            String accessToken = auth.get("access_token");

            for(Booking endedBooking : endedBookings){


                String vehicleId = endedBooking.getScooterId();
                List<Trip> trips = getTrips(accessToken, vehicleId, pjUrl);
                if(trips == null || trips.isEmpty()){
                    continue;
                }
//                ArrayNode trips = (ArrayNode) mapper.readTree(response)
//                        .path("trip_overview_list");
//                List<Trip> newTrips = mapper.convertValue(trips, new TypeReference<List<Trip>>(){});
//                if(trips.size() == 0){
//                    logger.info("No trips for booking: {}", endedBooking);
//                    continue;
//                }
                logger.info("number of trips found: {}", trips.size());
                trips.forEach(trip-> logger.info("trip: {}", trip));
                endedBooking.getTrips().addAll(trips);
                logger.info("appending trip to booking");
                endedBooking.save(endedBooking);
                logger.info("saving updated booking");
            }
        }catch(IOException e) {
            logger.info(e.getMessage());

        }catch(NullPointerException e){
            logger.info("access-token not found: {}", e.getMessage());

        }catch(ClassCastException e){

        } catch(Exception e){
            logger.info(e.getMessage());
        }

//        endedBookings.forEach((endedBooking)->{
//
//            String vehicleId = endedBooking.getScooterId();
//            try{
//                String response = getTrips(accessToken, vehicleId, pjUrl);
//                ArrayNode trips = (ArrayNode) mapper.readTree(response)
//                        .path("trip_overview_list");
//                List<Trip> newTrips = mapper.convertValue(trips, new TypeReference<List<Trip>>(){});
//                if(trips.size() == 0){
//                    logger.info("No trips for booking:" + endedBooking);
//                    return;
//                }
//                logger.info("number of trips found: " + trips.size());
//                endedBooking.getTrips().addAll(newTrips);
//                endedBooking.save(endedBooking);
//                logger.info("saving updated booking");
//
//            }catch(IOException e) {
//                logger.info(e.getMessage());
//            }catch(Exception e){
//                System.out.println(e.getMessage());
//            }
//        });
    }


    private List<Trip> getTrips(String accessToken, String vehicleId, String url) throws IOException, NullPointerException {

        if (accessToken == null) {
           logger.info("no token");
           throw new NullPointerException("access-token not found");
        }

        String fullUrl = url + "/vehicles/" + vehicleId + "/trips";
        //Todo: add query params for startDate and endDate
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(fullUrl)
                .addHeader("Content-type", "application/json")
                .addHeader("Authorization", accessToken)
                .build();
        Response response = client.newCall(request).execute();
        ArrayNode trips = (ArrayNode) mapper.readTree(response.body().string())
                .path("trip_overview_list");
        return mapper.convertValue(trips, new TypeReference<List<Trip>>() {});
    }


    private Map<String, String> getAuth(String audience, String actor, String clientSecret, String authUrl) throws IOException{

        ObjectMapper objectmapper = new ObjectMapper();

        String stringBody = String.format("{\"audience\":\"%s\", \"grant_type\":\"client_credentials\"," +
                        " \"client_id\":\"%s\",\"client_secret\":\"%s\"}", audience, actor, clientSecret);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, stringBody);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(authUrl)
                .addHeader("Content-type", "application/json")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return objectmapper.readValue(response.body().string(), new TypeReference<Map<String, String>>() {});
    }


    private String getSecret(String region, String secretName) {

        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.

        String secret;
        String decodedBinarySecret;

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            logger.info("Secrets Manager can't decrypt the protected secret text using the provided KMS key: {}", e.getMessage());
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InternalServiceErrorException e) {
            logger.info("An error occurred on the server side: {}", e.getMessage());
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidParameterException e) {
            logger.info("invalid value for a parameter was provided: {}", e.getMessage());
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (InvalidRequestException e) {
            logger.info("parameter value provided that is not valid for the current state of the resource: {}", e.getMessage());
            // Deal with the exception here, and/or rethrow at your discretion.
            throw e;
        } catch (ResourceNotFoundException e) {
            logger.info("We can't find the resource that you asked for: {}", e.getMessage());
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

