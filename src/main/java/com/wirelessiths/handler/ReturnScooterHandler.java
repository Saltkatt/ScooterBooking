package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.UpdateBookingRequest;
import com.wirelessiths.exception.UnableToListBookingsException;
import com.wirelessiths.exception.UnableToUpdateException;
import com.wirelessiths.service.ExceptionHandlingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
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

                logger.info(input);

                Booking booking = new Booking();
                UpdateBookingRequest updateBookingRequest;

                JsonNode body = null;
                JsonNode pathParameters = null;
                JsonNode jsonNode = mapper.valueToTree(input);

                if (jsonNode.hasNonNull("body")) {
                    body = mapper.valueToTree(jsonNode.get("body"));
                }
                if (jsonNode.hasNonNull("pathParameters")) {
                    pathParameters = mapper.valueToTree(jsonNode.get("pathParameters"));
                }

                booking =  Optional.ofNullable(pathParameters.get("id").asText()).map(ExceptionHandlingService.handlingFunctionWrapper(booking::get, IOException.class)).orElseThrow(() -> new UnableToUpdateException("Incorrect booking id provided in pathparameters"));
                logger.info(booking.toString());
                logger.info(body.toString());
                updateBookingRequest = Optional.ofNullable(body).map(ExceptionHandlingService.handlingFunctionWrapper(b -> mapper.treeToValue(b, UpdateBookingRequest.class), IOException.class)).orElseThrow(() -> new UnableToUpdateException("Incorrect body provided"));



                UpdateBookingHandler.setBookingProperties(updateBookingRequest, booking);
                booking.save(booking);


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

