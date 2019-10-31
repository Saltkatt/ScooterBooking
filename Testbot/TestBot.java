package com.wirelessiths;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBot {


    private static final Logger LOGGER = LoggerFactory.getLogger(TestBot.class);


    //Sends the expected Slack message

    private SlackMessage slackMessage = SlackMessage.builder()
            .channel("Lisa Marie (ITHS)")
            .username("Lisa Marie (ITHS)")
            .text("The Lambda recieved an error.")
            .icon_emoji(":twice:")
            .build();


    private SlackMessage positiveSlackMessage = SlackMessage.builder()
            .channel("Lisa Marie (ITHS)")
            .username("Lisa Marie (ITHS)")
            .text("The Test bot is up and running!")
            .icon_emoji(":twice:")
            .build();


    private String createBooking(String token) throws Exception {

        OkHttpClient client = new OkHttpClient();

        //JSON input data
        String jsonInputString = "{\n" +
                "\"scooterId\" : \"Ljpc54\",\n" +
                "\"startTime\" : \"2019-08-30T15:00:36.739Z\",\n" +
                "\"endTime\" : \"2019-08-30T16:00:36.739Z\"\n" +
                "}";

        //Post request with Authentication
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputString);
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
        if (response.code() == 201) {
            LOGGER.info("\nCongratulations on booking your Scooter trip. Safe travel!");

        } else {
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
            throw new Exception("Delete booking failed.");


        }

        return responseBody;

    }


    public boolean performTesting(String userName, String password) {

        boolean success = false;
        try {
            String token = loginUsingOkHttp(userName, password);

            //String response1 creates new booking
            String response1 = createBooking(token);
            //String response2 retrieves a booking according to booking id
            String response2 = readBooking(token, response1);

            String jsonInputString = "{\"startTime\" : \"2019-10-25T14:50:30.427Z\"}";

            //String response3 updates a booking
            String response3 = updateBooking(token, response2, jsonInputString);
            //String response4 deletes the updated booking
            String response4 = deleteBooking(token, response3);

            // log that it worked fine
            success = true;
            LOGGER.info("Success!");

        } catch (Exception e) {
            // log that it failed, including error message from exception
            success = false;
            LOGGER.info("Test run failed " + e.getMessage());
        }
        return success;
    }

    public String loginUsingOkHttp(String userName, String password) throws Exception {


        userName = "lisamarie";
        password = "MfC:VD9Vx";


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
