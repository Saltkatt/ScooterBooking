package com.wirelessiths.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.*;
import java.util.Collections;
import java.util.Map;

public class ActivateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());
    //TODO: how auto cancel booking if not activate in given timespan
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        int deadlineSeconds = 60 * 15;//temp hardcoded deadline for when booking is should be checked out
        Response responseBody;
        Booking booking = new Booking();
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.ofHours(-2));
        JsonNode body;

        try{
            body = new ObjectMapper().readTree((String) input.get("body"));

            String scooterId = body.get("scooterId").asText();
            String bookingId = body.get("bookingId").asText();
            String userId = AuthService.getUserInfo(input, "sub");

            booking = booking.get(bookingId);
            Instant startTime = booking.getStartTime();

            if( !booking.getUserId().equals(userId) || !booking.getScooterId().equals(scooterId) ) {
                responseBody = new Response("request userId or scooterId does not match corresponding booking values");
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

            if (!now.isAfter(startTime) || !now.isBefore(startTime.plusSeconds(deadlineSeconds)) ||
                    !booking.getTripStatus().equals(TripStatus.WAITING_TO_START)) {

                responseBody = new Response("booking start time has not passed or booking has already been activated, now: " +
                        now + " booking start time: " + startTime + " deadline: " +
                        startTime.plusSeconds(deadlineSeconds));

                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
            booking.setTripStatus(TripStatus.IN_PROGRESS);
            booking.update(booking);
            //unlock scooter
            //do some other stuff

            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(booking)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch(IOException e ){

            logger.info(e.getMessage());
            responseBody = new Response("I/O Error: " + e.getMessage());

            return ApiGatewayResponse.builder()
                    .setStatusCode(502)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        } catch(Exception e){

            logger.info(e.getMessage());
            responseBody = new Response("Error: " + e.getMessage());

            return ApiGatewayResponse.builder()
                    .setStatusCode(502)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
