package com.wirelessiths.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.AuthService;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;
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

        String responseMessage = null;
        try{
            Response responseBody;
            int deadlineSeconds = 60 * 15;//hardcoded deadline for when booking is should be checked out
            JsonNode body = null;
            Booking booking = new Booking();
            Instant now = LocalDateTime.now().toInstant(ZoneOffset.ofHours(-2));

            //get relevant info from request body
            body = new ObjectMapper().readTree((String) input.get("body"));
            String scooterId = body.get("scooterId").asText();
            String bookingId = body.get("bookingId").asText();
            String userId = AuthService.getUserInfo(input, "sub");//comes with token sent by frontend
            //String userId = body.get("userId").asText();//temp from body for testing without setting gateway auth

            //compare values from request with values from booking so that scooterId match etc
            booking = booking.get(bookingId);
            if( booking.getUserId().equals(userId) && booking.getScooterId().equals(scooterId) ) {//is this check needed?

                //check that booking is in a valid state for activation
                Instant startTime = booking.getStartTime();
                if (now.isAfter(startTime) && now.isBefore(startTime.plusSeconds(deadlineSeconds)) &&
                        booking.getTripStatus().equals(TripStatus.WAITING_TO_START)) {

                    //update booking
                    booking.setTripStatus(TripStatus.IN_PROGRESS);
                    booking.update(booking);
                    //unlock scooter
                    //do some other stuff

                    return ApiGatewayResponse.builder()
                            .setStatusCode(200)
                            .setObjectBody(booking)
                            .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                            .build();
                }
                responseMessage = "booking start time has not passed or booking has already been activated, now: " + now + " booking start time: " + startTime + " deadline: " + startTime.plusSeconds(deadlineSeconds);

            }else{
                responseMessage = "request userId or scooterId does not match corresponding booking values";
            }
            return ApiGatewayResponse.builder()
                    .setStatusCode(409)
                    .setObjectBody(new Response(responseMessage, input))
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch(IOException e){

            logger.info(e.getMessage());
            responseMessage = "Error: " + e.getMessage();

            return ApiGatewayResponse.builder()
                    .setStatusCode(502)
                    .setObjectBody(new Response(responseMessage, input))
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
