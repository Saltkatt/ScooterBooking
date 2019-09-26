package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.exception.UnableToListBookingsException;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;



/**
 *This class allows users or admin to retrieve a list of all existing bookings.
 */
public class ListBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = LogManager.getLogger(this.getClass());

    enum queryEnum {
     scooterId,
        userId,
        startDate
    }

	/**
	 * This method connects to the ApiGatewayResponse and request handler to allow the retrieval of all bookings.
	 * @param input contains all booking information.
	 * @param context
	 * @return
	 */
	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		try {

			//logger.info(input.toString());

            //Query key prioritization at the moment (higher prio means that that key is queried and the others are filtered):
            //1. Date
            //2. UserId (all queries with userId require admin or that your token uuid matches the one in the query
            //3. ScooterId

            /*
             * Handles the following cases:
             * -userId & scooterId & date
             * -userId & scooterId
             * -userId & date
             * -date & scooterId
             * -date or userId or scooterId by themselves
             * -queryParams is empty
             * -queryParams contains other items than scooterId, userId and date. It then filters out unsearchable parameters and tries the query again.
             * -If no params are eligible for query, return all bookings.
             */

            Map<String,String> queryStringParameters = null;

            boolean isAdmin = AuthService.isAdmin(input);
            String tokenUserId = AuthService.getUserId(input);

			if(input.containsKey("queryStringParameters")) {
                queryStringParameters = (Map<String, String>) input.get("queryStringParameters");
            }

            //Check that the user is authorized, can only view their own bookings or need to be admin. If not authorized send back 403.
            if(Optional.ofNullable(queryStringParameters).isPresent()) {
                if (queryStringParameters.containsKey(queryEnum.userId.toString()) && !isAuthorized(isAdmin, queryStringParameters.get(queryEnum.userId.toString()), tokenUserId)) {
                    Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                    return ApiGatewayResponse.builder()
                            .setStatusCode(403)
                            .setObjectBody(responseBody)
                            .build();
                }
            }

            Booking booking = new Booking();
            List<Booking> bookings = retrieveBookings(queryStringParameters, booking);


			// send the response back
			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(bookings)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();

		} catch(UnableToListBookingsException ex){
			logger.error("Error in listing bookings: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

			// send the error response back
			Response responseBody = new Response("Error in listing bookings due to state: " + ex.getMessage(), input);
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
			Response responseBody = new Response("Error in I/O when listing bookings: " + ex.getMessage(), input);
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

	private boolean isAuthorized(boolean isAdmin, String queryUserId, String tokenUserId){
        return isAdmin || queryUserId.equals(tokenUserId);
    }

    @Nullable
    public List<Booking> retrieveBookings(Map<String, String> queryStringParameters, Booking booking) throws IOException {

        if (!Optional.ofNullable(queryStringParameters).isPresent()) {
           return booking.list();
        }
            Map<String, String> validKeyParams = new HashMap<>();
            Map<String, String> filter = new HashMap<>();
            //Only pass through supported query params
            queryStringParameters.forEach((k, v) -> {
                for (queryEnum e : queryEnum.values()) {
                    if (k.equals(e.toString())) {
                        validKeyParams.put(k, v);
                    }
                }
            });
            //if no valid keys are found return all
            if (validKeyParams.isEmpty()){
                return booking.list();
            }
            if (validKeyParams.containsKey(queryEnum.startDate.toString())) {
                if (validKeyParams.size() == 1) {
                  return booking.bookingsByDate(LocalDate.parse(validKeyParams.get(queryEnum.startDate.toString())));
                } else {
                    validKeyParams.forEach((k, v) -> {
                        if (!k.equals(queryEnum.startDate.toString())) {
                            filter.put(k, v);
                        }
                    });
                    return booking.bookingsByDate(LocalDate.parse(validKeyParams.get(queryEnum.startDate.toString())), filter);
                }
            } else if (validKeyParams.containsKey(queryEnum.userId.toString())) {
                if (validKeyParams.size() == 1) {
                    return booking.bookingsByUserId(validKeyParams.get(queryEnum.userId.toString()));
                } else {
                    validKeyParams.forEach((k, v) -> {
                        if (!k.equals(queryEnum.userId.toString())) {
                            filter.put(k, v);
                        }
                    });
                    return booking.bookingsByUserId(validKeyParams.get(queryEnum.userId.toString()), filter);
                }
            } else if (validKeyParams.containsKey(queryEnum.scooterId.toString())) {
                if (validKeyParams.size() == 1) {
                    return booking.bookingsByScooterId(validKeyParams.get(queryEnum.scooterId.toString()));
                } else {
                    validKeyParams.forEach((k, v) -> {
                        if (!k.equals(queryEnum.scooterId.toString())) {
                            filter.put(k, v);
                        }
                    });
                    return booking.bookingsByScooterId(validKeyParams.get(queryEnum.scooterId.toString()), filter);
                }
            }
        return booking.list();
    }
}
