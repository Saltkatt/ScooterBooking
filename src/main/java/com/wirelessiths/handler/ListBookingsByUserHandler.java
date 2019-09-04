package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.exception.UnableToListBookingsException;
import com.wirelessiths.service.AuthService;
import com.wirelessiths.dal.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ListBookingsByUserHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        try {

            logger.info(input);

            UpdateBookingHandler updateBookingHandler = new UpdateBookingHandler();



            Booking booking = new Booking();
            List<Booking> results = booking.getByUserId(AuthService.getUserInfo(input, "sub"));


            // send the response back
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(results)
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