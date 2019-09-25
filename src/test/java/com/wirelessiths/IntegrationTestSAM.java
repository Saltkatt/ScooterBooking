package com.wirelessiths;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrationTestSAM {

    // deploy locally - SAM
    // test bookings
    // test CRUD

    /**
     * HTTP request av api:er ex: http://127.0.0.1:3000/bookings/{id} [DELETE]
     * don't need authorisation from cognito.
     * may need user info sent in anyway.
     * if running locally do tests.
     */

   /* public static void integrationTest() {


    }*/

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

        // print result.
        System.out.println(response.toString());
        assertEquals(String.valueOf(responseCode), 200, responseCode);
        //assertEquals(response.)

        //return response.toString();

    }

    public void createBookingTest() throws IOException{
        // ListBookingFunction
        URL url = new URL("http://127.0.0.1:3000/bookings");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/" + "json");

        //need token
        //need body


    }

    public void deleteBookingTest(){


    }





}
