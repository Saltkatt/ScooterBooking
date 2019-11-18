package com.wirelessiths;


import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;


public class TestBot {


    private static final Logger LOGGER = LoggerFactory.getLogger(TestBot.class);

    //Sends the expected Slack messages

    private SlackMessage passedSlackMessage = SlackMessage.builder()
            .channel("Lisa Marie (ITHS)")
            .username("Lisa Marie (ITHS)")
            .text("The Test Bot is up and running!")
            .icon_emoji(":twice:")
            .build();

    private SlackMessage failedSlackMessage = SlackMessage.builder()
            .channel("Lisa Marie (ITHS)")
            .username("Lisa Marie (ITHS)")
            .text("The Test Bot received an error.")
            .icon_emoji(":twice:")
            .build();


    private String createBooking(String token) throws Exception {


        OkHttpClient client = new OkHttpClient();

        Instant startTime = Instant.now().plus(5, ChronoUnit.HOURS);
        Instant endTime = Instant.now().plus(6, ChronoUnit.HOURS);


        //JSON input data
        String jsonInputStringInstant = "{\n" +
                "\"scooterId\" : \"9kxR9UDW\",\n" +
                "\"startTime\" : " + "\"" + startTime + "\",\n" +
                "\"endTime\"   : " + "\"" + endTime + "\"\n" +
                "}";

        System.out.println(jsonInputStringInstant);

        //Post request with Authentication
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputStringInstant);
        Request request = new Request.Builder()

                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .addHeader("Authorization", "Bearer " + token)
                .url("https://api.book.wirelessscooter.com/bookings/")
                .post(body)
                .build();

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        Response response = client.newCall(request).execute();

        String responseBody = response.body().string();
        LOGGER.info("Response when attempting to book Scooter " + responseBody);
        if (response.code() == 201) {
            SlackUtils.sendMessage(passedSlackMessage);
            LOGGER.info("\nCongratulations on booking your Scooter trip. Safe travel!");

        } else {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody,
                    System.getenv("phoneNumber"), smsAttributes);
            SlackUtils.sendMessage(failedSlackMessage);
            throw new Exception("Booking failed.");
        }

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        String bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }

        return bookingId;

    }

    private String readBooking(String token, String bookingId) throws Exception {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()

                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .addHeader("Authorization", "Bearer " + token)
                .url("https://api.book.wirelessscooter.com/bookings/" + bookingId)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        int responseCode = response.code();
        if (responseCode == 200) {
            LOGGER.info("\nYou have succeeded in retrieving your booking information!");
        } else {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody,
                    System.getenv("phoneNumber"), smsAttributes);
            SlackUtils.sendMessage(failedSlackMessage);
            throw new Exception("Failed reading booking");

        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }
        log(bookingId);

        return bookingId;
    }

    private void log(String msg) {
        if (LOGGER == null) {
            System.out.println(msg);
        } else {
            LOGGER.info(msg);
        }
    }

    private String updateBooking(String token, String bookingId, String jsonInputString) throws Exception {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputString);
        Request request = new Request.Builder()

                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .addHeader("Authorization", "Bearer " + token)
                .url("https://api.book.wirelessscooter.com/bookings/" + bookingId)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        log("\nfinished update request");
        if (response.code() == 200) {
            LOGGER.info("\nThe booking with booking id " + bookingId +
                    " has successfully been updated.");
        } else {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody,
                    System.getenv("phoneNumber"), smsAttributes);
            SlackUtils.sendMessage(failedSlackMessage);
            throw new Exception("Update booking failed.");
        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        bookingId = "fakeBookingId";
        String newStartTime = node.get("startTime").asText();
        log("\n newStartTime: " + newStartTime);
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }

        return bookingId;

    }

    private String deleteBooking(String token, String bookingId) throws Exception {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()

                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .addHeader("Authorization", "Bearer " + token)
                .url("https://api.book.wirelessscooter.com/bookings/" + bookingId)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        if (response.code() == 204) {
            LOGGER.info("\nYou have successfully deleted your booking with" +
                    " booking id: " + bookingId + "." + " See you next time!" + "\n");

        } else {
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody,
                    System.getenv("phoneNumber"), smsAttributes);
            SlackUtils.sendMessage(failedSlackMessage);
            throw new Exception("Delete booking failed.");


        }

        return responseBody;

    }

    public boolean performTesting(String userName, String password) {

        boolean success = false;
        boolean bookingComplete = false;
        String bookingIdToDelete = null;
        String tokenForCleanup = null;

        try {
            String token = loginUsingOkHttp(userName, password);

            //String bookingId1 creates new booking
            String bookingId1 = createBooking(token);
            bookingComplete = true;
            bookingIdToDelete = bookingId1;
            tokenForCleanup = token;
            //String bookingId2 retrieves a booking according to booking id
            String bookingId2 = readBooking(token, bookingId1);

            Instant startTime = Instant.now().plus(5, ChronoUnit.HOURS);

            String jsonInputStringInput = "{\n" +
                    "\"startTime\" : " + "\"" + startTime + "\"\n" +
                    "}";

            System.out.println(jsonInputStringInput);

            //String bookingId3 updates a booking
            String bookingId3 = updateBooking(token, bookingId2, jsonInputStringInput);
            //String bookingId4 deletes the updated booking
            String bookingId4 = deleteBooking(token, bookingId3);

            //Log that the Test Bot succeeded
            success = true;
            LOGGER.info("Success!");

        } catch (Exception e) {
            // Log that the Test Bot failed and include the error message from the Exception
            success = false;
            LOGGER.info("Test run failed " + e.getMessage());
            if (bookingComplete) {
                try {
                    String bookingId4 = deleteBooking(tokenForCleanup, bookingIdToDelete);
                    LOGGER.info("Cleanup succeeded.");
                } catch (Exception exc) {
                    LOGGER.warn("Cleanup failed.");
                }
            }
        }
        return success;
    }


    public String loginUsingOkHttp(String userName, String password) throws Exception {


        userName = System.getenv("userName");
        password = System.getenv("password");


        OkHttpClient client = new OkHttpClient();

        String jsonInputString = "{\n" +
                "   \"AuthParameters\" : {\n" +
                "      \"USERNAME\" : \"" + userName + "\",\n" +
                "      \"PASSWORD\" : \"" + password + "\"\n" +
                "   },\n" +
                "   \"AuthFlow\" : \"USER_PASSWORD_AUTH\",\n" +
                "   \"ClientId\" : \"1k9l1lr9qafkhhuqd5tnqldsqv\"\n" +
                "}\n";


        RequestBody body = RequestBody.create(MediaType.parse("application/x-amz-json-1.1"), jsonInputString);
        Request request = new Request.Builder()

                .addHeader("X-Amz-Target", "AWSCognitoIdentityProviderService.InitiateAuth")
                .url("https://cognito-idp.eu-west-1.amazonaws.com/")
                .post(body)
                .build();


        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        if (response.code() == 200) {
            LOGGER.info("\nYou have successfully logged in " + responseBody);
        } else {
            throw new Exception("Access denied");
        }

        ObjectMapper mapper = new ObjectMapper();


        JsonNode node = mapper.readTree(responseBody);
        String AccessToken;
        if (node.hasNonNull("AuthenticationResult")) {

            if (node.get("AuthenticationResult").hasNonNull("AccessToken")) {
                AccessToken = node.get("AuthenticationResult").get("AccessToken").asText();
                LOGGER.info("\nAccessToken: " + AccessToken);

            } else {
                throw new Exception("AccessToken not found.");
            }
        } else {
            throw new Exception("AuthenticationResult not found.");
        }


        return AccessToken;
    }

}
