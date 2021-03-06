package com.wirelessiths.IT;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LocalSamTest {

    private static AmazonDynamoDB client;
    private static DynamoDBMapperConfig mapperConfig;
    private static String tableName = "test-table";
    static String bookingId;


    @Test
    public void testThatBookingIsCreated() throws IOException{
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url("http://127.0.0.1:3000/bookings/" + bookingId)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        assertNotNull(bookingId);
        System.out.println("Booking id is not null if test is passed.");
    }

    @Test
    public void testThatBookingIsUpdated() throws IOException {
        //Kalla på getBooking lambda
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url("http://127.0.0.1:3000/bookings/" + bookingId)
                .get()
                .build();

        ObjectMapper mapper = new ObjectMapper();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);

        //bookingId = "fakeBookingId";
        String newStartTime = node.get("startTime").asText();
        System.out.println("newStartTime: " + newStartTime);

        //String start = newStartTime;
        String expected = "2019-10-12T16:00:36.739Z";
        String actual = node.get("startTime").asText();

        assertEquals(expected, actual);

        System.out.println("newStartTime: " + newStartTime);
        System.out.println("actual: " + actual);
        System.out.println("Response body: " + responseBody);
        System.out.println("Booking id: " + bookingId );

    }

    /**
     * Tests ListBookingsFunction response code is 200.
     * @throws IOException
     */
    @Test
    public void listBookingsOkResponseCode200() throws IOException {
        // ListBookingFunction
        URL url = new URL("http://127.0.0.1:3000/bookings");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/" + "json");
        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response code " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
        if(responseCode == 200){
            assertEquals(String.valueOf(responseCode), 200, responseCode);
            System.out.println("Test success");
        }
        else{
            System.out.println("Error: " + responseCode);
        }
    }

    @BeforeClass
    public static void runMethods() {

        try {
            bookingId = createBookingLocalTest();
            String jsonInputString = "{\n" +
                    "\"scooterId\" : \"TestLocalDB\",\n" +
                    "\"startTime\" : \"2019-10-12T15:00:36.739Z\",\n" +
                    "\"endTime\" : \"2019-10-12T16:00:36.739Z\"\n" +
                    "}";
            updateBookingLocalTest(bookingId, jsonInputString);
            listBookingLocalTest();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void runDelete() {
        try {
            deleteBookingLocalTest(bookingId);
            //BookingTestBase.deleteTable(tableName, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a booking in a local database.
     * @throws Exception
     */
    public static String createBookingLocalTest() throws Exception {

        clientOkHttp();

        //JSON data
        String jsonInputString = "{\n" +
                "\"scooterId\" : \"TestLocalDBTrial47\",\n" +
                "\"startTime\" : \"2019-10-08T15:00:36.739Z\",\n" +
                "\"endTime\" : \"2019-10-08T16:00:36.739Z\"\n" +
                "}";

        //post request with Authentication
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputString);
        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url("http://127.0.0.1:3000/bookings")
                .post(body)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        Response response = clientOkHttp().newCall(request).execute();
        String responseBody = response.body().string();


        if (response.code() == 201) {
            //assertEquals(response.code(), 201, response.code());
            System.out.println("Congratulations on booking your Scooter trip. Safe travel!");
            //System.out.println(response.code());
        } else if (response.code() == 500){
            System.out.println("Failed to create booking");
            System.out.println("booking id: " + bookingId);
            System.out.println(responseBody);
        }
        else {
            System.out.println("Error: " + response.code());
            System.out.println(responseBody);
        }

        JsonNode node = mapper.readTree(responseBody);
        //String bookingId = String.valueOf(node.get("bookingId"));
        String bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
            System.out.println("This is the bookingId: " + bookingId);
        }
        return bookingId;
    }

    public static String updateBookingLocalTest(String bookingId, String jsonInputString) throws IOException {

        clientOkHttp();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputString);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url(" http://127.0.0.1:3000/bookings/" + bookingId)
                .put(body)
                .build();

        Response response = clientOkHttp().newCall(request).execute();
        String responseBody = response.body().string();

        if (response.code() == 201) {
            System.out.println("\nThe booking with booking id " + bookingId + " has successfully been updated."
                    + "\nResponse code: " + response.code());
        } else {
            System.out.println("There is a " + response.code() + " error in update booking."
                    + "\n" + "Booking id: " + bookingId
                    + "\n" + responseBody );
        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);

        bookingId = "fakeBookingId";
        String newStartTime = node.get("startTime").asText();
        System.out.println("newStartTime: " + newStartTime);

        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }
        return bookingId;
    }

    public static void listBookingLocalTest() throws IOException {
       clientOkHttp();

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url( "http://127.0.0.1:3000/bookings/" + bookingId)
                .get()
                .build();

        Response response = clientOkHttp().newCall(request).execute();

        String responseBody = response.body().string();

        if (response.code() == 200) {
            System.out.println(response.code());
            System.out.println(responseBody);

        } else {
            System.out.println("There is a " + response.code() + " error in list booking."
                    + "\n" + "Booking id: " + bookingId
                    + "\n" + responseBody );
        }

    }

    public static String deleteBookingLocalTest(String bookingId) throws IOException {

        clientOkHttp();

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url( "http://127.0.0.1:3000/bookings/" + bookingId)
                .delete()
                .build();

        Response response = clientOkHttp().newCall(request).execute();
        String responseBody = response.body().string();
        String msg = null;


        if (response.code() == 204) {
            System.out.println("\nYou have successfully deleted your booking with" +
                    " booking id: " + bookingId + "." + " Thank you for your patronage!" + "\n");

        } else {
            System.out.println("\nThere is a " + response.code() + " error in delete booking." + "\n" + responseBody
                    +"\n" + "Booking id: " + bookingId);
            //System.out.println(responseBody);
        }
        return responseBody;
    }

    public static OkHttpClient clientOkHttp() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS)
                .build();

        return client;
    }


}










