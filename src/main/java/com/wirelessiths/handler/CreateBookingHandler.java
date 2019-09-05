package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.TripStatus;
import com.wirelessiths.exception.CouldNotCreateBookingException;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles save requests and implements RequestHandler and ApiGatewayResponse.
 */
public class CreateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
     * This method connects to the ApiGatewayResponse and request handler to allow the creation of individual bookings.
     * @param input contains the body of information required for the booking.
     * @param context
     * @return
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            Booking booking = new Booking();

            booking.setScooterId(body.get("scooterId").asText());

            booking.setUserId(AuthService.getUserInfo(input, "sub"));
            booking.setStartTime(Instant.parse(body.get("startTime").asText()));
            booking.setEndTime(Instant.parse(body.get("endTime").asText()));
            booking.setStartTime(Instant.parse(body.get("startTime").asText()));
            booking.setEndTime(Instant.parse(body.get("endTime").asText()));
            booking.setDate(LocalDate.parse(body.get("date").asText()));
            booking.setTripStatus(TripStatus.WAITING_TO_START);

            if(booking.validateBooking(booking).size() == 0){//nr of bookings that incoming booking infringes on needs to be 0
                booking.save(booking);
                return ApiGatewayResponse.builder()
                        .setStatusCode(201)
                        .setObjectBody(booking)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
            Response responseBody = new Response("Scooter with id: " + booking.getScooterId() + "is not available for the selected timespan", input);

            return ApiGatewayResponse.builder()
                    .setStatusCode(409)
                    .setObjectBody(responseBody)
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
