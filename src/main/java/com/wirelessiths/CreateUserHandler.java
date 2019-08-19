package com.wirelessiths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.Booking;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Map;

public class CreateUserHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = Logger.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

      try {
          // get the 'body' from input
          JsonNode body = new ObjectMapper().readTree((String) input.get("body"));

          // create the Booking object for post
          Booking booking = new Booking();
          //booking.setSelectedScooter(body.get("id").asText());
		  booking.setBooker(body.get("username").asText());
          booking.setStartTimer(body.get("firstName").asText());
          booking.setEndTime(body.get("lastName").asText());
          booking.setEndPos(body.get("email").asText());
          booking.setStartPos(body.get("password").asText());
          booking.setPersonalIdentificationNumber(body.get("personalIdentificationNumber").asText());
          booking.save(booking);

          // send the response back
      		return ApiGatewayResponse.builder()
      				.setStatusCode(200)
      				.setObjectBody(booking)
      				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
      				.build();

      } catch (Exception ex) {
          logger.error("Error in saving user: " + ex);

          // send the error response back
    			Response responseBody = new Response("Error in saving user: ", input);
    			return ApiGatewayResponse.builder()
    					.setStatusCode(500)
    					.setObjectBody(responseBody)
    					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
    					.build();
      }
	}
}
