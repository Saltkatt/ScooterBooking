package com.wirelessiths.monitor;

<<<<<<< HEAD
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.BookingStatus;
import com.wirelessiths.s3.Settings;
import com.wirelessiths.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wirelessiths.service.SNSService.getAmazonSNSClient;
import static com.wirelessiths.service.SNSService.sendSMSMessage;

public class MonitorStartedBookings {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private Dotenv dotenv = Dotenv.load();

    public void lambdaHandler(){

        try{
            Settings settings = Settings.getSettings();
            int deadlineSeconds = settings.getNotCheckedOut();

            Booking booking = new Booking();
            List<Booking> startedBookings = booking.bookingsByStartTime(deadlineSeconds);

            if(startedBookings.isEmpty()){
                return;
            }

            logger.info("number of bookings not checked out within time-limit: {} ", startedBookings.size());
            for(Booking startedBooking: startedBookings){
                startedBooking.setBookingStatus(BookingStatus.CANCELLED);
                logger.info("canceling booking: {}", startedBooking);
                startedBooking.save(startedBooking);
                logger.info("saving booking");
                String message = "Your booking was cancelled due to not being activated within the given timespan";
                sendMessage(message, booking, dotenv.get("USER_POOL_ID"));
            }

        }catch(IOException e) {
            logger.info("error when saving booking: {}", e.getMessage());

        }catch(Exception e) {
            logger.info(e.getMessage());
        }
    }

    private void sendMessage(String message, Booking booking, String userPoolId){
        String phoneNumber = UserService.getUserPhoneNumber(booking.getUserId(), userPoolId);
        AmazonSNSClient snsClient = getAmazonSNSClient();
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<>();
        //<set SMS attributes>
        sendSMSMessage(snsClient, message, phoneNumber, smsAttributes);
=======
import com.wirelessiths.dal.Booking;

import java.util.List;

public class MonitorStartedBookings {

    public void lambdaHandler(){

        Booking booking = new Booking();
        List<Booking> endedBookings = null;

        try{
           // endedBookings = booking.bookingsByStartTime();

        }catch(Exception e) {
            //logger.info(e.getMessage());
        }

        if(endedBookings == null || endedBookings.isEmpty()){
            //logger.info("No ended bookings");
            return;
        }

>>>>>>> fix looping bug in MonitorEndedBookingsTemp, change name from MonitorEndedBookingsFake to ..BookingsTemp
    }
}
