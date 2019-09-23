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
import com.wirelessiths.service.HTTPGetService;
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

public class MonitorEndedBookingsTemp {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private HTTPGetService getRequest = new HTTPGetService();

    private Dotenv dotenv = Dotenv.load();
    private String baseUrl = dotenv.get("BASE_URL");
    private String tripEndpoint = dotenv.get("TRIP_ENDPOINT");
    private String authHeader = dotenv.get("AUTH");
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    //complete fake endedBookingsMonitor
    //get trips from today
    //check if they are not cancelled
    //connect trips to bookings
    //save trips to bookings

    //complete real endedBookingsMonitor
    //add credentials to secret manager
    //get secrets in code
    //query p&j for timespan
    //save trips to bookings




    public void doStuff(){

        //check if ended uncancelled bookings
        //if not log and return
        //else log and check p&j for trips
        //if no trips log and return
        //else log and append trip to booking

        Booking booking = new Booking();
        List<Booking> endedBookings = null;

        try{
            endedBookings = booking.bookingsByEndTime();

        }catch(Exception e) {
            logger.info(e.getMessage());
        }

        if(endedBookings == null || endedBookings.isEmpty()){
            logger.info("No ended bookings");
            return;
        }
        logger.info("number of bookings ended: " + endedBookings.size());

        endedBookings.forEach((endedBooking)->{

//            if(endedBooking.getBookingStatus().equals(BookingStatus.CANCELLED)){//do this check on dao method instead?
//                return;
//            }
            //endedBooking.setBookingStatus(BookingStatus.COMPLETED);
            //logger.info("setting bookingStatus to 'COMPLETED': " + endedBooking);

            String url = String.format("%s/%s%s", baseUrl, endedBooking.getScooterId(), tripEndpoint);
            logger.info("booking ended: " + endedBooking);
            String queryUrl = url + "?startDate=" + endedBooking.getBookingDate();

            try{
                String result = getRequest.run(queryUrl, authHeader);
                ArrayNode trips = (ArrayNode) objectMapper.readTree(result)
                        .path("trip_overview_list");

                if(trips.size() == 0){
                    logger.info("No trips for booking:" + endedBooking);
                    //endedBooking.save(endedBooking);
                    String phoneNumber = UserService.getUserPhoneNumber(endedBooking.getUserId(), System.getenv("USER_POOL_ID"));
                    AmazonSNS snsClient = getAmazonSNSClient();
                    String message = "No trip registered for your booking, if you didnt use the scooter, please cancel the booking next time";
                    Map<String, MessageAttributeValue> smsAttributes =
                            new HashMap<String, MessageAttributeValue>();
                    //<set SMS attributes>
                    logger.info("sending angry sms");
                    sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
                    return;
                }
                logger.info("number of trips found: " + trips.size());
                List<Trip> newTrips = objectMapper.convertValue(trips, new TypeReference<List<Trip>>(){});

                AtomicReference<Double> totalDistance = new AtomicReference<>((double) 0);
                newTrips.forEach(trip->{
                    logger.info("checking for match..");
                    if(!trip.getStartTime().isAfter(endedBooking.getStartTime()) ||
                        !trip.getEndTime().isBefore(endedBooking.getEndTime().plusSeconds(60 * 5))) {
                        logger.info("trip doesnt match");
                        return;
                    }
                    String phoneNumber = UserService.getUserPhoneNumber(endedBooking.getUserId(), System.getenv("USER_POOL_ID"));
                    AmazonSNS snsClient = getAmazonSNSClient();
                    double totalDistance = endedBooking.getTrips().stream().mapToDouble(Trip::getTotalDistanceMeter).sum();
                    String message = "Thank you for completing your trip. You traveled " + totalDistance + " meters";
                    Map<String, MessageAttributeValue> smsAttributes =
                            new HashMap<String, MessageAttributeValue>();
                    //<set SMS attributes>
                    logger.info("sending happy sms");
                    logger.info("match found, trip: " + trip);
                    sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
                    endedBooking.getTrips().add(trip);
                    logger.info("appending trip to booking");
                });
                    totalDistance.updateAndGet(v -> v + trip.getTotalDistanceMeter());
                    endedBooking.getTrips().add(trip);
                    logger.info("appending trip to booking");
                });

                String phoneNumber = UserService.getUserPhoneNumber(endedBooking.getUserId(), System.getenv("USER_POOL_ID"));
                AmazonSNSClient snsClient = getAmazonSNSClient();

                //Strin
                String message = "Thank you for completing your trip. You traveled " + Math.ceil(totalDistance.get()) + " meters";
                Map<String, MessageAttributeValue> smsAttributes =
                        new HashMap<String, MessageAttributeValue>();
                //<set SMS attributes>
                logger.info("sending happy sms");
                sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);

                try{
                endedBooking.update(endedBooking);
                logger.info("saving updated booking");
                }catch(IOException e){
                    logger.info("error saving updated booking: " + e.getMessage());
                }
            }catch(IOException e){
                logger.info("IOException: " + e.getMessage());
            }catch(Exception e){
                logger.info("something went wrong: " + e.getMessage());
                logger.info(e);
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
