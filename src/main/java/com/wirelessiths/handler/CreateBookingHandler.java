package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.exception.CouldNotCreateBookingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles save requests and implements RequestHandler and ApiGatewayResponse.
 */
public class CreateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    /**
	 * This method connects to the ApiGatewayResponse and request handler to allow the creation of individual bookings.
	 * @param input contains the body of information required for the booking.
	 * @param context
	 * @return
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {


      try {
          // get the 'body' from input
          JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
          Booking booking = new Booking();

		  booking.setScooterId(body.get("scooterId").asText());
		  booking.setUserId(body.get("userId").asText());
		  booking.setStartTime(LocalDateTime.parse(body.get("startTime").asText()));
          booking.setEndTime(LocalDateTime.parse(body.get("endTime").asText()));

          booking.save(booking);

          // send the response back
      		return ApiGatewayResponse.builder()
      				.setStatusCode(200)
      				.setObjectBody(booking)
      				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
      				.build();

      } catch (CouldNotCreateBookingException ex) {
			logger.error("Error in creating booking: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		} catch (JsonProcessingException ex) {
			logger.error("Error in JSON processing" + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back

			Response responseBody = new Response("Error in JSON processing: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		} catch (IOException ex) {
			logger.error("Error: IOException" + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back

			Response responseBody = new Response("Error in creating booking due to I/O: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		}catch (Exception ex){
            logger.error("Error unknown Exception" + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back


            Response responseBody = new Response("Error in creating booking due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
	}
}
