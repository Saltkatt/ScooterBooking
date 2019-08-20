package com.wirelessiths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.exception.CouldNotCreateBookingException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class CreateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'body' from input
			JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

			// create the Booking object for post
			Booking booking = new Booking();
			// set userID
			//booking.setUserId(userId);
			// set message
			booking.setMessage(body.get("message").asText());
			//
			booking.save(booking);

			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(booking)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		} catch (CouldNotCreateBookingException ex) {
			logger.error("Error in creating booking: " + ex);

			// send the error response back
			Response responseBody = new Response("Error in creating booking: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		} catch (JsonProcessingException ex) {
			logger.error("Error in JSON processing" + ex);

			// send the error response back
			Response responseBody = new Response("Error in JSON processing: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		} catch (IOException ex) {
			logger.error("Error: IOException" + ex);

			// send the error response back
			Response responseBody = new Response("Error in creating booking due to I/O: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
					.build();

		}catch (Exception ex){
            logger.error("Error unknown Exception" + ex);

            // send the error response back
            Response responseBody = new Response("Error in creating booking due to unknown exception: ", input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
	}
}
