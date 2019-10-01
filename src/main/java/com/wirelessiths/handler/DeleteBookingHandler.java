package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.exception.CouldNotDeleteBookingException;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * This class handles delete requests and implements RequestHandler and ApiGatewayResponse.
 */
public class DeleteBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = LogManager.getLogger(this.getClass());


	/**
	 * This method connects to the ApiGatewayResponse and request handler to allow the deletion of individual bookings.
	 * @param input contains path parameters.json that allow users to delete bookings connected to a bookingId.
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


			Booking booking = new Booking().get(bookingId);
			// get the Booking by id

			if (!AuthService.isAuthorized(isAdmin, booking.getUserId(), tokenUserId)) {
				Response responseBody = new Response("Unauthorized. You can only delete your own bookings or you need to have admin privilege", input);
				return ApiGatewayResponse.builder()
						.setStatusCode(403)
						.setObjectBody(responseBody)
						.build();
			}

			// get the Booking by id
			Boolean success = new Booking().delete(bookingId);

			// send the response back
			if (success) {
				return ApiGatewayResponse.builder()
						.setStatusCode(204)
						.build();
			} else {
				return ApiGatewayResponse.builder()
						.setStatusCode(404)
						.setObjectBody("Booking with id: '" + bookingId + "' not found.")
						.build();
			}
		} catch (CouldNotDeleteBookingException ex) {
			logger.error("Error in deleting booking: " + ex);
			logger.error(ex.getMessage());
			ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in deleting booking, state is null: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		} catch (IOException ex) {
			logger.error("Error: IOException " + ex);
			logger.error(ex.getMessage());
			ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in I/O: " + ex.getMessage(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();

		}catch (Exception ex) {
            logger.error("Error in deleting Booking: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

        // send the error response back
        Response responseBody = new Response("Unknown error in deleting Booking: " + ex.getMessage(), input);
        return ApiGatewayResponse.builder()
                .setStatusCode(500)
                .setObjectBody(responseBody)
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                .build();
    }
	}
}
