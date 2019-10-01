package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.*;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class UpdateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            // get the 'pathParameters' from input
            Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
            String bookingId = pathParameters.get("id");

            boolean isAdmin = AuthService.isAdmin(input);
            String tokenUserId = AuthService.getUserId(input);

            // get the Booking by id
            Booking booking = new Booking().get(bookingId);

            if (!AuthService.isAuthorized(isAdmin, booking.getUserId(), tokenUserId)) {
                Response responseBody = new Response("Unauthorized. You can only update your own bookings or you need to have admin privilege", input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(403)
                        .setObjectBody(responseBody)
                        .build();
            }

            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

            UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();

            //booking = UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);


            boolean isNew = false;
            Booking updatedBooking = null;


            // send the response back
            if (booking != null) {

                try {

                    try {
                        updateBookingRequest =  mapper.treeToValue(body, UpdateBookingRequest.class);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }


                    if ( body.has("endTime") || body.has("scooterId")) {

                        isNew = true;

                        updatedBooking = rewriteBooking(booking);
                    }

                    if(isNew)  {

                        if(updatedBooking != null) {

                            updatedBooking = setBookingProperties(updateBookingRequest, updatedBooking);
                            updatedBooking.save(updatedBooking);
                        }
                    }
                    else {

                        updatedBooking = booking;
                        updatedBooking = setBookingProperties(updateBookingRequest, updatedBooking);
                        updatedBooking.update(updatedBooking);
                    }



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
                        .setObjectBody(updatedBooking)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
            else {

                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Product with id: '" + bookingId + "' not found.")
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


        Pattern DATE_PATTERN = Pattern.compile(
                "^((2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26])))-02-29)$"
                        + "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
                        + "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"
                        + "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");

        Pattern TIME_PATTERN = Pattern.compile("([01]?[0-9]|2[0-3]):[0-5][0-9]");

        Pattern ISO_INSTANT = Pattern.compile(DATE_PATTERN + "T" + TIME_PATTERN + ".\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?\\d?Z");



        Optional.ofNullable(updateBookingRequest).ifPresent(optUpdateRequest -> {
                optUpdateRequest.getUserId().filter(s -> !s.isEmpty()).ifPresent(booking::setUserId);
                optUpdateRequest.getScooterId().filter(s -> !s.isEmpty()).ifPresent(booking::setScooterId);
                optUpdateRequest.getBookingId().filter(s -> !s.isEmpty()).ifPresent(booking::setBookingId);

                optUpdateRequest.getDate().ifPresent(n -> {
                    if (n.matches(String.valueOf(DATE_PATTERN))) {
                        try {
                            LocalDateConverter converter = new LocalDateConverter();
                            booking.setStartDate(converter.unconvert(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                optUpdateRequest.getStartTime().ifPresent(n -> {
                    if (!n.matches("")) {
                        try {
                            InstantConverter converter = new InstantConverter();
                            booking.setStartTime(converter.unconvert(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                optUpdateRequest.getEndTime().ifPresent(n -> {
                    if(!n.matches("")) {
                        try {
                            InstantConverter converter = new InstantConverter();
                            booking.setEndTime(converter.unconvert(n));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } );





        return booking;
    }


    public static Booking rewriteBooking(Booking booking) {

        boolean isDeleted = false;

        try {
            Booking newBooking = new Booking().get(booking.getBookingId());
            isDeleted = booking.delete(booking.getBookingId());

            if(isDeleted) {
                return newBooking;
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
