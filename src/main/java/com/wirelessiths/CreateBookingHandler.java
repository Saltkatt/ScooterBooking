package com.wirelessiths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.dal.Booking;
import org.apache.log4j.Logger;

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
          // user.setId(body.get("id").asText());
		  //String userId = context.getIdentity().getIdentityId();


		  //booking.setScooterId(body.get("scooterId").asText());
		  //booking.setStartTime(body.get("startTime"));
		  //booking.setStartTime(LocalDateTime.parse(body.get("startTime").asText()));
		  //booking.setEndTime(LocalDateTime.parse(body.get("endTime").asText()));
		  booking.setUserId(userId);
		  booking.setMessage(body.get("message").asText());

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
