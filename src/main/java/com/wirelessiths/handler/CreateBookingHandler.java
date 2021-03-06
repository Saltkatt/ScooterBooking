package com.wirelessiths.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.BookingStatus;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.s3.Settings;
import com.wirelessiths.service.AuthService;
import com.wirelessiths.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.wirelessiths.service.SNSService.getAmazonSNSClient;
import static com.wirelessiths.service.SNSService.sendSMSMessage;

/**
 * This class handles save requests and implements RequestHandler and ApiGatewayResponse.
 */
public class CreateBookingHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {
            // get the 'body' from input
            JsonNode body = new ObjectMapper().readTree((String) input.get("body"));
            Booking booking = new Booking();
            booking.setScooterId(body.get("scooterId").asText());
            booking.setUserId(AuthService.getUserId(input));
            booking.setStartTime(Instant.parse(body.get("startTime").asText()));
            booking.setEndTime(Instant.parse(body.get("endTime").asText()));
            booking.setBookingStatus(BookingStatus.VALID);

            int maxDuration = 7200;
            int buffer = 300;
            int maxAllowedBookings = 3;

            if(!System.getenv("ENVIRONMENT").equals("test")){
                Settings settings = Settings.getSettings();
                 maxDuration = settings.getMaxDuration();
                 buffer = settings.getBuffer();
                 maxAllowedBookings = settings.getMaxBookings();
            }
            String message;
            double duration = Duration.between(booking.getStartTime(), booking.getEndTime()).getSeconds();

            if(duration > maxDuration) {

                message = "Requested timespan is more than max allowed length,  allowed length: " + maxDuration/60f +
                        " min, requested length: " +  duration/60f + " min";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                        .build();
            }

            if(booking.bookingsByUserId(booking.getUserId()).size() >= maxAllowedBookings) {

                message = "User has reached max number of allowed concurrent bookings";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                        .build();
            }

            if(!booking.validateBooking(booking, maxDuration, buffer).isEmpty()){//returns list of infringing bookings

                message =  "Scooter with id: " + booking.getScooterId() + " is not available for the selected timespan";
                return ApiGatewayResponse.builder()
                        .setStatusCode(409)
                        .setObjectBody(message)
                        .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                        .build();

            }
            booking.save(booking);
            if(!System.getenv("ENVIRONMENT").equals("test")){

                String userMessage = String.format("Booking confirmation for startdate: %s and enddate: %s", booking.getStartTime(), booking.getEndTime());
                sendMessage(userMessage, booking, System.getenv("USER_POOL_ID"));

            }
            return ApiGatewayResponse.builder()
                    .setStatusCode(201)
                    .setObjectBody(booking)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        } catch (JsonProcessingException ex) {
            logger.error(String.format("Error in JSON processing: %s ", ex.getMessage()));

            Response responseBody = new Response("Error in JSON processing: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        } catch (IOException ex) {
            logger.error(String.format("Error: IOException: %s", ex.getMessage()));

            Response responseBody = new Response("Error in creating booking due to I/O: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();

        }catch(NullPointerException ex){

            logger.info(ex.getMessage());

            Response responseBody = new Response("Error in creating booking due to empty or invalid request body: " + ex.getStackTrace());
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();


        }catch (Exception ex){

            logger.error(String.format("Error unknown Exception: %s", ex));

            Response responseBody = new Response("Error in creating booking due to unknown exception: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("Booking System", "Wireless Scooter"))
                    .build();
        }
    }

    private void sendMessage(String message, Booking booking, String userPoolId){
        String phoneNumber = UserService.getUserPhoneNumber(booking.getUserId(), userPoolId);
        AmazonSNS snsClient = getAmazonSNSClient();
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<>();
        //<set SMS attributes>
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
    }
}
