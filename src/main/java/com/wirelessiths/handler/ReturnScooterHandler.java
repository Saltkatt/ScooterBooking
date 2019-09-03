package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.AuthService;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;
import com.wirelessiths.dal.UpdateBookingRequest;
import com.wirelessiths.exception.UnableToListBookingsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Takes a booking object in body. Finds booking in dynamodb and updates tripStatus to scooter_returned
 */

    public class ReturnScooterHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

        private final Logger logger = LogManager.getLogger(this.getClass());

        @Override
        public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
            try {

                ObjectMapper mapper = new ObjectMapper();

                logger.info(input);

                Booking booking = new Booking();
                UpdateBookingRequest updateBookingRequest = new UpdateBookingRequest();
                Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
                JsonNode body = mapper.readTree((String) input.get("body"));
                String bookingId =  pathParameters.get("id");
                booking = booking.get(bookingId);

                try {
                    updateBookingRequest =  mapper.treeToValue(body, UpdateBookingRequest.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }


                booking = UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);

                booking.save(booking);


                // send the response back
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(booking)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();

            } catch(UnableToListBookingsException ex){
                logger.error("Error in listing bookings: " + ex);
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in listing bookings due to state: ", input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();

            }catch (IOException ex){
                logger.error("Error in listing bookings due to I/O: " + ex);
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in I/O when listing bookings: ", input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();

            } catch(Exception ex) {
                logger.error("Error in listing users: " + ex);
                logger.error(ex.getMessage());
                ex.printStackTrace();

                // send the error response back
                Response responseBody = new Response("Error in listing bookings: " + ex.getMessage(), input);
                return ApiGatewayResponse.builder()
                        .setStatusCode(500)
                        .setObjectBody(responseBody)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }
        }

}

