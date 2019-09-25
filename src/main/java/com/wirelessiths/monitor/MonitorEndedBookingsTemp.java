package com.wirelessiths.monitor;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.trip.Trip;
import com.wirelessiths.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.wirelessiths.service.SNSService.getAmazonSNSClient;
import static com.wirelessiths.service.SNSService.sendSMSMessage;
import static java.lang.Math.ceil;

public class MonitorEndedBookingsTemp {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private Dotenv dotenv = Dotenv.load();
    private String baseUrl = dotenv.get("BASE_URL");
    private String authHeader = dotenv.get("AUTH");
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);


    public void lambdaHandler() {

        try {

            Booking booking = new Booking();
            List<Booking>endedBookings = booking.bookingsByEndTime();

            if (endedBookings.isEmpty()) {
                logger.info("No ended bookings");
                return;
            }
            logger.info("number of bookings ended: {} ", endedBookings.size());
            for (Booking endedBooking : endedBookings) {

                List<Trip> trips = getTrips(endedBooking);
                if (trips.isEmpty()) {
                    logger.info("No trips for booking: {}", endedBooking);
                    String message = "No trip registered for your booking, if you didnt use the scooter, please cancel the booking next time";
                    sendMessage(message, endedBooking, dotenv.get("USER_POOL_ID"));
                    logger.info("sending angry sms");

                    continue;
                }

                logger.info("number of trips found: {}", trips.size());
                double distanceTraveled = 0;

                for (Trip trip : trips) {

                    if (trip.getStartTime().isAfter(endedBooking.getStartTime()) &&
                            trip.getEndTime().isBefore(endedBooking.getEndTime().plusSeconds(60 * 5L))) {

                        distanceTraveled += trip.getTotalDistanceMeter();
                        endedBooking.getTrips().add(trip);
                        logger.info("appending matching trip to booking");
                    }
                }
                endedBooking.update(endedBooking);
                logger.info("saving updated booking");
                if(trips.isEmpty()){
                    String message = String.format("Thank you for completing your trip. You traveled %s meters", Math.ceil(distanceTraveled));
                    sendMessage(message, endedBooking, dotenv.get("USER_POOL_ID"));
                    logger.info("sending happy sms");
                }
            }
        }catch(Exception e){
            logger.info(e);
        }
    }

    private List<Trip> getTrips(Booking booking) throws IOException{

        String url = String.format("%s/%s%s", baseUrl, booking.getScooterId(), "/trips");
        String queryUrl = url + "?startDate=" + booking.getStartDate();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(queryUrl)


                .header("Authorization", authHeader)
                .build();

        Response response = client.newCall(request).execute();
        ArrayNode trips = (ArrayNode) objectMapper.readTree(response.body().string())
                .path("trip_overview_list");

        if(trips.size() == 0){
            return new ArrayList<>();
        }
        return objectMapper.convertValue(trips, new TypeReference<List<Trip>>(){});
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