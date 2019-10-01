package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.exception.BookingDoesNotExistException;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * This class handles get requests and implements RequestHandler and ApiGatewayResponse.
 */
@SuppressWarnings("DuplicatedCode")
public class GetBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = LogManager.getLogger(this.getClass());


	/**
	 * This method connects to the ApiGatewayResponse and request handler to allow the retrieval of individual bookings.
	 * @param input contains path parameters.json that allow users to retrieve bookings connected to a bookingId.
	 * @param context
	 * @return
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

		try {
			// get the 'pathParameters' from input
			Map<String,String> pathParameters =  (Map<String,String>)input.get("pathParameters");
			String bookingId = pathParameters.get("id");

			boolean isAdmin = AuthService.isAdmin(input);
			String tokenUserId = AuthService.getUserId(input);

			// get the Booking by id
			Booking booking = new Booking().get(bookingId);

			if (!AuthService.isAuthorized(isAdmin, booking.getUserId(), tokenUserId)) {
				Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
				return ApiGatewayResponse.builder()
						.setStatusCode(403)
						.setObjectBody(responseBody)
						.build();
			}

			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(booking)
					.build();

		} catch (BookingDoesNotExistException ex) {
			logger.error("Error in retrieving Booking as booking  is null: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in retrieving Booking: " + ex.getMessage(), input);
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
			Response responseBody = new Response("Error in I/O when retrieving booking: " + ex.getMessage(), input);
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
			Response responseBody = new Response("Unknown error in retrieving Booking: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		}

	}
}
