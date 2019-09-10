package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.BookingStatus;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.s3.Settings;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles save requests and implements RequestHandler and ApiGatewayResponse.
 */
public class CreateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            Booking booking = new Booking();
            logger.info("1");
            booking.setScooterId(body.get("scooterId").asText());
            logger.info("2");

            booking.setUserId(AuthService.getUserId(input));
            logger.info("3");
            booking.setStartTime(Instant.parse(body.get("startTime").asText()));
            logger.info("4");
            booking.setEndTime(Instant.parse(body.get("endTime").asText()));
            logger.info("5");
            booking.setBookingStatus(BookingStatus.VALID);
            logger.info("6");

            Settings settings = Settings.getSettings();
            logger.info("7");
            int maxDuration = settings.getMaxDuration();
            logger.info("8");
            int buffer = settings.getBuffer();
            logger.info("9");
            int maxAllowedBookings = settings.getMaxBookings();
            logger.info("10");
            String message;
            double duration = Duration.between(booking.getStartTime(), booking.getEndTime()).getSeconds();
            logger.info("11");

            if(duration > maxDuration) {

                message = "Requested timespan is more than max allowed length,  allowed length: " + maxDuration/60f +
                        " min, requested length: " +  duration/60f + " min";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

            if(booking.getByUserId(booking.getUserId()).size() >= maxAllowedBookings) {

                message = "User has reached max number of allowed bookings";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

            if(booking.validateBooking(booking, maxDuration, buffer).size() > 0){//returns list on infringing bookings

                message =  "Scooter with id: " + booking.getScooterId() + " is not available for the selected timespan";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();

            }
            booking.save(booking);
            return ApiGatewayResponse.builder()
                    .setStatusCode(201)
                    .setObjectBody(booking)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (JsonProcessingException ex) {
            logger.error("Error in JSON processing" + ex.getMessage());

            Response responseBody = new Response("Error in JSON processing: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        } catch (IOException ex) {
            logger.error("Error: IOException" + ex.getMessage());

            Response responseBody = new Response("Error in creating booking due to I/O: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        }catch (Exception ex){

            logger.error("Error unknown Exception" + ex.getMessage());

            Response responseBody = new Response("Error in creating booking due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
    }
}
