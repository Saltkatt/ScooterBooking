package com.wirelessiths.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;
import com.wirelessiths.dal.User;
import com.wirelessiths.exception.UnableToUpdateException;
import com.wirelessiths.service.ExceptionHandlingService;
import com.wirelessiths.service.SESService;
import com.wirelessiths.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Takes a booking object in body. Finds booking in dynamodb and updates tripStatus to scooter_returned
 */

    public class ReturnScooterHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

        private final Logger logger = LogManager.getLogger(this.getClass());

        @Override
        public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


            try {

                ObjectMapper mapper = new ObjectMapper();

                String mailSubject = "Scooter returned";
                String htmlbody = "<h1>Thank you for returning the scooter!</h1>"
                        + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
                        + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
                        + "AWS SDK for Java</a>";
                String textBody = "Thank you for returning the scooter!";

                Booking booking = new Booking();
                JsonNode body = null;
                String scooterId;
                String bookingId;
                String userId;

                body = new ObjectMapper().readTree((String) input.get("body"));

                if(body.hasNonNull("scooterId") && body.hasNonNull("bookingId") && body.hasNonNull("userId")) {
                     scooterId = body.get("scooterId").asText();
                     bookingId = body.get("bookingId").asText();
                     userId = body.get("userId").asText();
                } else {
                    throw new UnableToUpdateException("scooterId, bookingId or userId not provided");
                }


                booking =  Optional.ofNullable(bookingId).map(ExceptionHandlingService.handlingFunctionWrapper(booking::get, IOException.class)).orElseThrow(() -> new UnableToUpdateException("Incorrect booking id or booking does not exist"));

                if(!booking.getScooterId().equals(scooterId) || !booking.getUserId().equals(userId)){
                    throw new UnableToUpdateException("sent scooterId or bookingId doesnt match with the id's in the booking");
                }

                List<User> users = UserService.listUsers(booking.getUserId(), System.getenv("USER_POOL_ID"));
                String recipient = null;
                if (!users.isEmpty()) {
                    recipient = users.get(0).getEmail();
                }

                if(booking.getTripStatus() == TripStatus.IN_PROGRESS){
                    booking.setTripStatus(TripStatus.COMPLETED);
                    booking.save(booking);
                    Optional.ofNullable(recipient).ifPresent(ExceptionHandlingService.handlingConsumerWrapper((r)-> SESService.sendEmail(SESService.defaultFrom, r, mailSubject, htmlbody, textBody), IOException.class));
                } else {
                    throw new UnableToUpdateException("can not return a scooter that is not activated");
                }

                // send the response back
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(booking)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();

            } catch(UnableToUpdateException ex){
                logger.error("Error in updateing bookings: " + ex.getMessage());
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in updating booking bookings due to state: " + ex.getMessage(), input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .build();

            }catch (IOException ex){
                logger.error("Error in updating bookings due to I/O: " + ex);
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in I/O when listing bookings: " + ex.getMessage(), input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .build();

            } catch(Exception ex) {
                logger.error("Error in listing users: " + ex.getMessage());
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in updateing booking: " + ex.getMessage(), input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .build();
            }
        }




}

