package com.wirelessiths.handler;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.dal.AuthService;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public class checkoutScooterHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        int checkoutLimitSeconds = 60 * 15;
        JsonNode body = null;
        Booking booking = new Booking();
        Instant now = Instant.now();
        try{
            body = new ObjectMapper().readTree((String) input.get("body"));
            String scooterId = body.get("scooterId").asText();
            String bookingId = body.get("bookingId").asText();
            String userId = AuthService.getUserInfo(input, "sub");//comes with token sent by frontend

            booking = booking.get(bookingId);//is this check needed?
            if( booking.getUserId().equals(userId) && booking.getScooterId().equals(scooterId) ){

                Instant startTime = booking.getStartTime();
                if( now.isAfter(startTime) && now.isBefore(startTime.plusSeconds(checkoutLimitSeconds)) &&
                    booking.getTripStatus().equals(TripStatus.WAITING_TO_START) ){

                    booking.setTripStatus(TripStatus.IN_PROGRESS);
                    booking.update(booking);
                    //update booking
                    //unlock scooter
                    //do some other stuff

                    return ApiGatewayResponse.builder()
                            .setStatusCode(200)
                            .setObjectBody(booking).setObjectBody("hejsan!")
                            .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                            .build();
                }
            }else{
                //invalid checkout request
            }

        }catch(IOException e){
            logger.info(e.getMessage());
        }

        return null;
    }
}
