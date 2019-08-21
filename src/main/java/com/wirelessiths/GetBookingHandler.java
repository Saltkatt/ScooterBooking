package com.wirelessiths;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.exception.BookingDoesNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class GetBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'pathParameters' from input
			Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
			String bookingId = pathParameters.get("id");

			// get the Booking by id
			Booking booking = new Booking().get(bookingId);

			// send the response back
			if (booking != null) {
				return ApiGatewayResponse.builder()
						.setStatusCode(200)
						.setObjectBody(booking)
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
						.build();
			} else {
				return ApiGatewayResponse.builder()
						.setStatusCode(404)
						.setObjectBody("Booking with id: '" + bookingId + "' not found.")
						.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
						.build();
			}

		} catch (BookingDoesNotExistException ex) {
			logger.error("Error in retrieving Booking as booking  is null: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in retrieving Booking: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();

		} catch(IOException ex) {
			logger.error("Error in retrieving booking due to I/O: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in I/O when retrieving booking: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();

		}catch (Exception ex) {
			logger.error("Error in retrieving Booking: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Unknown error in retrieving Booking: ", input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		}

	}
}
