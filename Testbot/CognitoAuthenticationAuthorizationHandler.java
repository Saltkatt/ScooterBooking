/*
 * CognitoAuthenticationAuthorizationHandler authenticates a token in Cognito for usage in Lambda.
 * The methods are created based on CRUD functionality using Json for input.
 * Used the OkHttp3 library for the HTTP Requests.
 */


import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CognitoAuthenticationAuthorizationHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {


    private Context globalContext;


    //Interface for creating default implementation
    private AWSCognitoIdentityProvider cognito = AWSCognitoIdentityProviderClientBuilder.defaultClient();

    //Getting all userpools and their names by ID
    List<UserPoolDescriptionType> userPools =
            cognito.listUserPools(new ListUserPoolsRequest().
                    withMaxResults(20)).getUserPools();


    private String createBooking(String token) throws Exception {

        OkHttpClient client = new OkHttpClient();

        //JSON input data
        String jsonInputString = "{\n" +
                "\"scooterId\" : \"Zbxsjp9x\",\n" +
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
            globalContext.getLogger().log("\nCongratulations on booking your Scooter trip. Safe travel!");
        } else {
            globalContext.getLogger().log("\nThe answer is not 201. ");
            globalContext.getLogger().log(responseBody);
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody, System.getenv("phoneNumber"), smsAttributes);

        }

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        String bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }

        return bookingId;

    }

    private String readBooking(String token, String bookingId) throws IOException {

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
            globalContext.getLogger().log("\nYou have succeeded in retrieving your booking information!");
        } else {
            globalContext.getLogger().log("\nThe error code in read booking is: " + responseCode);
            globalContext.getLogger().log(responseBody);
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody,
                    System.getenv("phoneNumber"), smsAttributes);
        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }
        globalContext.getLogger().log(bookingId);

        return bookingId;
    }

    private String updateBooking(String token, String bookingId, String jsonInputString) throws IOException {

        globalContext.getLogger().log("\nrunning updateBooking");
        globalContext.getLogger().log("\njsonInputString = " + jsonInputString);
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

        globalContext.getLogger().log("\nfinished update request");
        if (response.code() == 200) {
            globalContext.getLogger().log("\nThe booking with booking id " + bookingId +
                    " has successfully been updated.");
        } else {
            globalContext.getLogger().log("There is a 401 error in update booking.");
            globalContext.getLogger().log(responseBody);
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody, System.getenv("phoneNumber"), smsAttributes);
        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);
        bookingId = "fakeBookingId";
        String newStartTime = node.get("startTime").asText();
        globalContext.getLogger().log("newStartTime: " + newStartTime);
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }

        return bookingId;

    }

    private String deleteBooking(String token, String bookingId) throws IOException {

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
            globalContext.getLogger().log("\nYou have successfully deleted your booking with" +
                    " booking id: " + bookingId + "." + " See you next time!" + "\n");

        } else {
            globalContext.getLogger().log("There is a 404 error in delete booking.");
            globalContext.getLogger().log(responseBody);
            Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
            SNSService.sendSMSMessage(SNSService.getAmazonSNSClient(), responseBody, System.getenv("phoneNumber"), smsAttributes);

        }

        return responseBody;

    }

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


        globalContext = context;

        try {
            String userName = System.getenv("userName");
            String password = System.getenv("password");


            //Stating the aws region
            String userPoolId = System.getenv("userPoolId");


            ListUserPoolClientsResult response =
                    cognito.listUserPoolClients(new ListUserPoolClientsRequest().
                            withUserPoolId(userPoolId).withMaxResults(1));

            UserPoolClientType userPool =
                    cognito.describeUserPoolClient(new DescribeUserPoolClientRequest().
                            withUserPoolId(userPoolId).withClientId(response.getUserPoolClients().
                            get(0).getClientId())).getUserPoolClient();


            //Authenticating the user and pass username and password
            Map<String, String> authParams = new HashMap<>(2);
            authParams.put("USERNAME", userName);
            authParams.put("PASSWORD", password);
            AdminInitiateAuthRequest authRequest =
                    new AdminInitiateAuthRequest().withClientId(userPool.getClientId()).
                            withUserPoolId(userPool.getUserPoolId()).withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH).
                            withAuthParameters(authParams);
            AdminInitiateAuthResult result = cognito.adminInitiateAuth(authRequest);
            AuthenticationResultType auth = result.getAuthenticationResult();
            String token = auth.getAccessToken();


            //String response1 creates new booking
            String response1 = createBooking(token);
            //String response2 retrieves a booking according to booking id
            String response2 = readBooking(token, response1);

            String jsonInputString = "{\"startTime\" : \"2019-09-25T14:40:20.468Z\"}";

            //String response3 updates a booking
            String response3 = updateBooking(token, response2, jsonInputString);
            //String response4 deletes the updated booking
            String response4 = deleteBooking(token, response3);


            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody("The Testbot ran smoothly. ")
                    .build();

        } catch (final Exception exception) {

            exception.printStackTrace();

            return ApiGatewayResponse.builder()
                    .setStatusCode(501)
                    .setObjectBody(exception.toString())
                    .build();

        }


    }


}


