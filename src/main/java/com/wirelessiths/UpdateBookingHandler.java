package com.wirelessiths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class UpdateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        logger.info(input);
        try {

            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String productId = pathParameters.get("id");

            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            // get the Product by id
            Booking booking = new Booking().get(productId);

            // send the response back
            if (booking != null) {

                String temp = "temp ";

                try {

                   // booking.setScooterId(body.get("scooterId").asText());
                    //temp = body.get("scooterId").asText();
                    //booking.setBookingId(body.get("bookingId").asText());
                    //booking.setBookingId(String.valueOf(Optional.ofNullable(body.get("bookingId").asText())));
                    //booking.setScooterId(String.valueOf(Optional.ofNullable(body.get("scooterId").asText())));
                    //booking.setUserId(String.valueOf(Optional.ofNullable(body.get("userId").asText())));
                   // booking.setStartTime(LocalDateTime.parse(String.valueOf(Optional.ofNullable(body.get("startTime").asText()))));
                   // booking.setEndTime(LocalDateTime.parse(String.valueOf(Optional.ofNullable(body.get("endTime").asText()))));


                    //Optional<String> scooterId = Optional.ofNullable(body.get("scooterId").asText());
                    //scooterId.ifPresent(n -> booking.setScooterId(n));


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
                   //     booking.setStartTime(LocalDateTime.parse(body.get("startTime").asText()));
                    }

                    if (body.has("endTime") && !body.get("endTime").asText().isEmpty()) {
                   //     booking.setEndTime(LocalDateTime.parse(body.get("endTime").asText()));
                    }

                    booking.update(booking);

                } catch (Exception e) {

                    logger.error("Error in retrieving product: " + e);

                    // send the error response back
                    Response responseBody = new Response(temp + "Error in updating product: " + e.getMessage(), input);
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
}
