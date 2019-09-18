package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class IntegrationTestSAM {

    // deploy locally - SAM
    // test bookings
    // test CRUD

    /**
     * HTTP request av api:er ex: http://127.0.0.1:3000/bookings/{id} [DELETE]
     * don't need authorisation from cognito.
     * may need user info sent in anyway.
     */

   /* public static void integrationTest() {


    }*/
    @Test
    public void createBookingTest() throws IOException {
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

        // print result.
        System.out.println(response.toString());

        //return response.toString();

    }




}