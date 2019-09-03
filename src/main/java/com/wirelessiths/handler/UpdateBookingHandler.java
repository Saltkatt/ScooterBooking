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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class UpdateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {

            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String productId = pathParameters.get("id");
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            // get the Product by id
            Booking booking = new Booking().get(productId);

            // send the response back
            if (booking != null) {

                try {

                    if (body.has("bookingId") && !body.get("bookingId").asText().isEmpty()) {
                        booking.setBookingId(body.get("bookingId").asText());
                    }

                    if (body.has("scooterId") && !body.get("scooterId").asText().isEmpty()) {
                        booking.setScooterId(body.get("scooterId").asText());
                    }

                    if (body.has("userId") && !body.get("userId").asText().isEmpty()) {
                        booking.setUserId(body.get("userId").asText());
                    }

                    if (body.has("startTime") && !body.get("startTime").asText().isEmpty()) {
                             booking.setStartTime(Instant.parse(body.get("startTime").asText()));
                    }

                    if (body.has("endTime") && !body.get("endTime").asText().isEmpty()) {
                             booking.setEndTime(Instant.parse(body.get("endTime").asText()));
                    }

                    booking.update(booking);

                } catch (Exception e) {

                    logger.error("Error in retrieving product: " + e);

                    // send the error response back
                    Response responseBody = new Response( "Error in updating product: " + e.getMessage(), input);
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
            Response responseBody = new Response("Error in retrieving product: " + ex.getMessage(), input);
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

        Optional.ofNullable(updateBookingRequest).ifPresent(optUpdateRequest -> {
                optUpdateRequest.getUserId().ifPresent(booking::setUserId);
                optUpdateRequest.getScooterId().ifPresent(booking::setScooterId);
                optUpdateRequest.getBookingId().ifPresent(booking::setBookingId);

                optUpdateRequest.getDate().ifPresent(n -> {
                    if (n.matches(""))
                        try {
                            LocalDateConverter converter = new LocalDateConverter();
                            booking.setDate(converter.unconvert(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                });

                optUpdateRequest.getStartTime().ifPresent(n -> {
                    try {
                        InstantConverter converter = new InstantConverter();
                        booking.setStartTime(converter.unconvert(n));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                optUpdateRequest.getEndTime().ifPresent(n -> {
                    try {
                        InstantConverter converter = new InstantConverter();
                        booking.setEndTime(converter.unconvert(n));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                optUpdateRequest.getTripStatus().ifPresent(n -> {
                    if (n.equals("WAITING_TO_START") || n.equals("IN_PROGRESS") || n.equals("COMPLETED") || n.equals("SCOOTER_NOT_RETURNED")) {
                        TripStatus tripStatus = TripStatus.valueOf(n);
                        booking.setTripStatus(tripStatus);
                    }

                });


            } );




        return booking;
    }

}
