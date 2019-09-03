package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class UpdateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        logger.info(input);

        try {

            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String productId = pathParameters.get("id");

            // get the Product by id
            Booking booking = new Booking().get(productId);

            // send the response back
            if (booking != null) {

                try {
                    booking.setBookingId(String.valueOf(Optional.ofNullable(body.get("username").asText())));
                    booking.setScooterId(String.valueOf(Optional.ofNullable(body.get("firstName").asText())));
                    booking.setUserId(String.valueOf(Optional.ofNullable(body.get("lastName").asText())));
                    booking.setStartTime(Instant.parse(String.valueOf(Optional.ofNullable(body.get("email").asText()))));
                    booking.setEndTime(Instant.parse(String.valueOf(Optional.ofNullable(body.get("password").asText()))));
                    booking.update(booking);

                } catch (Exception e) {

                    logger.error("Error in retrieving booking: " + e);

                    // send the error response back
                    Response responseBody = new Response("Error in updating booking: ", input);
                    return ApiGatewayResponse.builder()
                            .setStatusCode(500)
                            .setObjectBody(responseBody)
                            .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                            .build();
                }

                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(booking)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
            else {

                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Product with id: '" + productId + "' not found.")
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

        }catch (Exception ex) {

            logger.error("Error in retrieving product: " + ex);

            // send the error response back
            Response responseBody = new Response("Error in retrieving product: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }
    }

    /**
     * Makes a booking object from updateBookingRequest. Should be used for updating object so that if no new info is inputted, the old information will persist.
     * @param updateBookingRequest - the booking request in where the new information is stored.
     * @param booking - the booking that you want to update.
     * @return updated booking
     */
    public static Booking setBookingProperties(UpdateBookingRequest updateBookingRequest, Booking booking) {

        updateBookingRequest.getUserId().ifPresent(booking::setUserId);
        updateBookingRequest.getScooterId().ifPresent(booking::setScooterId);
        updateBookingRequest.getBookingId().ifPresent(booking::setBookingId);

        updateBookingRequest.getDate().ifPresent(n -> {
            if(n.matches(""))
                try {
                    LocalDateConverter converter = new LocalDateConverter();
                    booking.setDate(converter.unconvert(n));
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });

        updateBookingRequest.getStartTime().ifPresent(n -> {
            try {
                InstantConverter converter = new InstantConverter();
                booking.setStartTime(converter.unconvert(n));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        updateBookingRequest.getEndTime().ifPresent(n -> {
            try {
                InstantConverter converter = new InstantConverter();
                booking.setEndTime(converter.unconvert(n));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        updateBookingRequest.getTripStatus().ifPresent(n -> {
            if(n.equals("WAITING_TO_START") || n.equals("IN_PROGRESS") || n.equals("COMPLETED") || n.equals("SCOOTER_NOT_RETURNED")) {
                TripStatus tripStatus = TripStatus.valueOf(n);
                booking.setTripStatus(tripStatus);
            }

        });

        return booking;
    }

}
