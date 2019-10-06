package com.wirelessiths.monitor;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

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

    private final String clientSecret = getSecret("eu-west-1", "client_secret");
    private final String audience = dotenv.get("AUDIENCE");
    private final String actor = dotenv.get("ACTOR");
    private final String authUrl = dotenv.get("PJ_AUTH_URL");
    private final String pjUrl = dotenv.get("PJ_URL");

    private static Dotenv dotenv = Dotenv.load();
    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    public void lambdaHandler(){

        Booking booking = new Booking();
        List<Booking> endedBookings = booking.bookingsByEndTime();

        if(endedBookings.isEmpty()){
            return;
        }
        logger.info("number of bookings ended: {}", endedBookings.size());

        try{
            Map<String, String> auth = getAuth(audience, actor, clientSecret, authUrl);
            String accessToken = auth.get("access_token");

            for(Booking endedBooking : endedBookings){

                String vehicleId = endedBooking.getScooterId();
                List<Trip> trips = getTrips(accessToken, vehicleId, pjUrl, endedBooking);
                if(trips == null || trips.isEmpty()){
                    continue;
                }

                logger.info("number of trips found: {}", trips.size());
                trips.forEach(trip-> logger.info("trip: {}", trip));
                endedBooking.getTrips().addAll(trips);
                logger.info("appending trip to booking");
                endedBooking.save(endedBooking);
                logger.info("saving updated booking");
            }
        }catch(JsonMappingException e) {
            logger.info("error deserializing trips: {}", e.getMessage());
        }catch(NullPointerException e){
            logger.info("access-token not found: {}", e.getMessage());

        }catch(ClassCastException e){

        }catch(IOException e) {
            logger.info(e.getMessage());
        } catch (Exception e){
            logger.info(e.getMessage());
        }
    }


    private List<Trip> getTrips(String accessToken, String vehicleId, String url, Booking endedBooking) throws IOException {

        if (accessToken == null) {
           logger.info("no token");
           throw new NullPointerException("access-token not found");
        }
        String queryParams = String.format("?startDate=%s&endDate=%s", endedBooking.getStartTime(), endedBooking.getEndTime());
        String fullUrl = url + "/vehicles/" + vehicleId + "/trips" + queryParams ;
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

         }catch (DecryptionFailureException e) {
            logger.info("Secrets Manager can't decrypt the protected secret text using the provided KMS key: {}", e.getMessage());
            throw e;
        } catch (InternalServiceErrorException e) {
            logger.info("An error occurred on the server side: {}", e.getMessage());
            throw e;
        } catch (InvalidParameterException e) {
            logger.info("invalid value for a parameter was provided: {}", e.getMessage());
            throw e;
        } catch (InvalidRequestException e) {
            logger.info("parameter value provided that is not valid for the current state of the resource: {}", e.getMessage());
            throw e;
        } catch (ResourceNotFoundException e) {
            logger.info("We can't find the resource that you asked for: {}", e.getMessage());
            throw e;
        }

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();

            //return substring of secret value only
            return secret.substring(secret.indexOf(':') + 2, secret.indexOf('}') -1);
        }
        else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
            return decodedBinarySecret;
        }
    }
}

