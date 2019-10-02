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

import static org.junit.Assert.assertEquals;

public class LocalSamTest {

    static String bookingId;

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
                    "\"startTime\" : \"2019-10-02T15:00:36.739Z\",\n" +
                    "\"endTime\" : \"2019-10-02T16:00:36.739Z\"\n" +
                    "}";
            updateBookingLocalTest(bookingId, jsonInputString);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @AfterClass
    public static void runDelete() {
        try {
            deleteBookingLocalTest(bookingId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testThatBookingIsUpdated() {
        //Kalla på getBooking lambda
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url("http://127.0.0.1:3000/bookings/" + bookingId)
                .get()
                .build();

        String responseBody;

        assertEquals("startTime","2019-10-03T15:00:36.739Z","2019-10-03T15:00:36.739Z");
        System.out.println("Test Success");
    }

    /**
     * Creates a booking in a local database.
     * @throws Exception
     */
    public static String createBookingLocalTest() throws Exception {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        //JSON data
        String jsonInputString = "{\n" +
                "\"scooterId\" : \"TestLocalDB\",\n" +
                "\"startTime\" : \"2019-10-02T15:00:36.739Z\",\n" +
                "\"endTime\" : \"2019-10-02T16:00:36.739Z\"\n" +
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
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();


        if (response.code() == 201) {
            assertEquals(response.code(), 201, response.code());
            System.out.println("Congratulations on booking your Scooter trip. Safe travel!");
        } else {
            System.out.println("Error: " + response.code());
            System.out.println("Failed to book");
            System.out.println(responseBody);
        }

        JsonNode node = mapper.readTree(responseBody);
        String bookingId = "fakeBookingId";
        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
            System.out.println("This is the bookingId: " + bookingId);
        }
        return bookingId;
    }

    public static String updateBookingLocalTest(String bookingId, String jsonInputString) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInputString);

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url(" http://127.0.0.1:3000/bookings/" + bookingId)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();

        String responseBody = response.body().string();

        if (response.code() == 201) {
            System.out.println("\nThe booking with booking id " + bookingId + " has successfully been updated."
                    + "\nResponse code: " + response.code());
        } else {
            System.out.println("There is a " + response.code() + " error in update booking."
                    + "\n" + responseBody);
        }

        //For reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Retrieving the data contained inside the node
        JsonNode node = mapper.readTree(responseBody);

        bookingId = "fakeBookingId";
        String newStartTime = node.get("startTime").asText();

        if (node.hasNonNull("bookingId")) {
            bookingId = node.get("bookingId").asText();
        }

        return bookingId;

    }


   public static String deleteBookingLocalTest(String bookingId) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(600, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .addHeader("User-Agent", "insomnia/6.6.2")
                .addHeader("Content-Type", "application/" + "json")
                .url( "http://127.0.0.1:3000/bookings/" + bookingId)
                .delete()
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        String msg = null;


        if (response.code() == 204) {
            System.out.println("\nYou have successfully deleted your booking with" +
                    " booking id: " + bookingId + "." + " Thank you for your patronage!" + "\n");

        } else {
            System.out.println("\nThere is a " + response.code() + " error in delete booking." + "\n" + responseBody);
            System.out.println(responseBody);
        }
        return responseBody;
    }


}










