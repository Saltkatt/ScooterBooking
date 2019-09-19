package com.wirelessiths.handler;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.exception.UnableToListBookingsException;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.service.AuthService;
import com.wirelessiths.service.SNSService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.wirelessiths.service.SNSService.getAmazonSNSClient;
import static com.wirelessiths.service.SNSService.sendSMSMessage;


/**
 *This class allows users or admin to retrieve a list of all existing bookings.
 */
public class ListBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private final Logger logger = LogManager.getLogger(this.getClass());

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

            Map<String,String> queryStringParameters = null;
            List<Booking> bookings = null;

            boolean isAdmin = AuthService.isAdmin(input);
            String tokenUserId = AuthService.getUserId(input);

			if(input.containsKey("queryStringParameters")) {
                queryStringParameters = (Map<String, String>) input.get("queryStringParameters");
            }


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
             * -queryParams contains other items than scooterId, userId and date
             */

            if (!Optional.ofNullable(queryStringParameters).isPresent()) {
                bookings = new Booking().list();
            }

			else if(queryStringParameters.containsKey("scooterId") || queryStringParameters.containsKey("userId") || queryStringParameters.containsKey("date")) {

                if (queryStringParameters.containsKey("scooterId") && queryStringParameters.containsKey("userId") && queryStringParameters.containsKey("date")) {

                    if(!isAuthorized(isAdmin, queryStringParameters.get("userId"), tokenUserId)) {
                        Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                        return ApiGatewayResponse.builder()
                                .setStatusCode(403)
                                .setObjectBody(responseBody)
                                .build();
                    }
                    String queryKey = "date";
                    String queryValue = queryStringParameters.get("date");
                    Map<String, String> filter = new HashMap<>();
                    //Copies all but the queryKey two the filter
                    queryStringParameters.forEach((s1,s2)->{
                        if (!s1.equals(queryKey)){
                            filter.put(s1,s2);
                        }
                    } );
                    bookings = new Booking().getByDateWithFilter(LocalDate.parse(queryValue), filter);
                } else if(queryStringParameters.containsKey("userId") && queryStringParameters.containsKey("scooterId")){

                    if(!isAuthorized(isAdmin, queryStringParameters.get("userId"), tokenUserId)) {
                        Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                        return ApiGatewayResponse.builder()
                                .setStatusCode(403)
                                .setObjectBody(responseBody)
                                .build();
                    }

                    String queryKey = "userId";
                    String queryValue = queryStringParameters.get("userId");
                    Map<String, String> filter = new HashMap<>();
                    //Copies all but the queryKey two the filter
                    queryStringParameters.forEach((s1,s2)->{
                        if (!s1.equals(queryKey)){
                            filter.put(s1,s2);
                        }
                    } );
                    bookings = new Booking().getByUserIdWithFilter(queryValue, filter);
                }

                else if(queryStringParameters.containsKey("date") && queryStringParameters.containsKey("scooterId") || queryStringParameters.containsKey("date") && queryStringParameters.containsKey("userId") ){

                    if(queryStringParameters.containsKey("userId") && !isAuthorized(isAdmin, queryStringParameters.get("userId"), tokenUserId)) {
                        Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                        return ApiGatewayResponse.builder()
                                .setStatusCode(403)
                                .setObjectBody(responseBody)
                                .build();
                    }


                    String queryKey = "date";
                    String queryValue = queryStringParameters.get("date");
                    Map<String, String> filter = new HashMap<>();
                    //Copies all but the queryKey two the filter
                    queryStringParameters.forEach((s1,s2)->{
                        if (!s1.equals(queryKey)){
                            filter.put(s1,s2);
                            logger.info(s1 + " : " + s2);
                        }
                    } );
                    bookings = new Booking().getByDateWithFilter(LocalDate.parse(queryValue), filter);
                }

                else if(queryStringParameters.size()==1){
                    if(queryStringParameters.containsKey("date")){
                        bookings = new Booking().getByDate(LocalDate.parse(queryStringParameters.get("date")));
                    } else if (queryStringParameters.containsKey("userId")){
                        if(!isAuthorized(isAdmin, queryStringParameters.get("userId"), tokenUserId)) {
                            Response responseBody = new Response("Unauthorized. You can only view your own bookings or you need to have admin privilege", input);
                            return ApiGatewayResponse.builder()
                                    .setStatusCode(403)
                                    .setObjectBody(responseBody)
                                    .build();
                        }
                        bookings = new Booking().getByUserId(queryStringParameters.get("userId"));
                    } else if (queryStringParameters.containsKey("scooterId")) {
                        bookings = new Booking().getByScooterId(queryStringParameters.get("scooterId"));
                    }
                }


			}

            else {
                    Response responseBody = new Response("Inserted query parameters are not supported, you can only use scooterId, userId and date", input);
                    return ApiGatewayResponse.builder()
                            .setStatusCode(404)
                            .setObjectBody(responseBody)
                            .build();
                }

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
}
