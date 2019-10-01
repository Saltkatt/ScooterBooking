package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.BookingStatus;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Map;

public class SetBookingStateHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {
// json request
//    {
//        "command": "start",
//        "scooterId" : "1234"
//    }
private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        String incomingUserId;
        String incomingBookingId;
        String incomingScooterId;
        String command;
        Response responseBody;
        String newState = null;
        Booking booking = new Booking();

        try{
           JsonNode body  = new ObjectMapper().readTree((String) input.get("body"));
           Map<String, String> pathParameters = (Map)input.get("pathParameters");

           incomingUserId = AuthService.getUserId(input);
           incomingBookingId = pathParameters.get("id");
           incomingScooterId = body.get("scooterId").asText();
           command = body.get("command").asText();
           boolean isAdmin = AuthService.isAdmin(input);
           booking = booking.get(incomingBookingId);

            if (!AuthService.isAuthorized(isAdmin, booking.getUserId(), incomingUserId)) {
                Response responseBodyunAuth = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(403)
                        .setObjectBody(responseBodyunAuth)
                        .build();
            }

           if(booking == null){
               responseBody = new Response("no booking found for bookingId: " + incomingBookingId);
               return ApiGatewayResponse.builder()
                       .setStatusCode(404)
                       .setObjectBody(responseBody)
                       .build();
           }

           Instant now = Instant.now();
           Instant startTime = booking.getStartTime();
           int deadlineSeconds = 60 * 10;

           if( !booking.getUserId().equals(incomingUserId) || !booking.getScooterId().equals(incomingScooterId) ) {
               responseBody = new Response("request userId or scooterId does not match corresponding booking values");
               return ApiGatewayResponse.builder()
                       .setStatusCode(400)
                       .setObjectBody(responseBody)
                       .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                       .build();
           }

           switch (command){
               case "activate":
                   if (!now.isAfter(startTime) || !now.isBefore(startTime.plusSeconds(deadlineSeconds)) ||
                                                !booking.getBookingStatus().equals(BookingStatus.VALID)) {

                       responseBody = new Response("booking is not in a valid state to be activated", input);
                       return ApiGatewayResponse.builder()
                               .setStatusCode(400)
                               .setObjectBody(responseBody)
                               .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                               .build();
                   }
                   booking.setBookingStatus(BookingStatus.ACTIVE);
                   newState = BookingStatus.ACTIVE.toString();
                   //do other stuff?
                   break;

               case "complete":
                   //validate if end is possible
                   if(!booking.getBookingStatus().equals(BookingStatus.ACTIVE)){

                       responseBody = new Response("booking is not in a valid state to be completed");
                       return ApiGatewayResponse.builder()
                               .setStatusCode(400)
                               .setObjectBody(responseBody)
                               .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                               .build();
                   }
                   //complete
                   booking.setBookingStatus(BookingStatus.COMPLETED);
                   newState = BookingStatus.COMPLETED.toString();

                   //do other stuff?
                   break;

               case "cancel":
                   //validate if cancel is possible
                   if(!booking.getBookingStatus().equals(BookingStatus.VALID)){

                       responseBody = new Response("booking is not in a valid state to be canceled");
                       return ApiGatewayResponse.builder()
                               .setStatusCode(400)
                               .setObjectBody(responseBody)
                               .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                               .build();
                   }
                   //cancel
                   booking.setBookingStatus(BookingStatus.CANCELLED);
                   newState = BookingStatus.CANCELLED.toString();

                   //do other stuff?
                   break;
           }
           responseBody = new Response("booking status set to: " + newState);
           booking.save(booking);
           return ApiGatewayResponse.builder()
                   .setStatusCode(200)
                   .setObjectBody(booking)
                   .setObjectBody(responseBody)
                   .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                   .build();

        }catch(JsonProcessingException e){
            String errorMessage = "Error converting request body into json: " + e.getMessage();
            logger.info(errorMessage);

            responseBody = new Response(errorMessage);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }
        catch(IOException e){
            String errorMessage = "I/0 Error: " + e.getMessage();
            logger.info(errorMessage);

            responseBody = new Response(errorMessage);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        }catch(Exception e){
            String errorMessage = "Something went wrong: " + e.getMessage();
            logger.info(errorMessage);

            responseBody = new Response(errorMessage);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }
}
