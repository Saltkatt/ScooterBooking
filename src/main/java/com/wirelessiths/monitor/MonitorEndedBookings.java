package com.wirelessiths.monitor;

import com.amazonaws.secretsmanager.caching.SecretCache;
import com.wirelessiths.dal.Booking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MonitorEndedBookings {

    private final Logger logger = LogManager.getLogger(this.getClass());

    //om quera på main table, behövs info om vilka skootrar som finns, så man kan göra en query per skooter
    //get matching booking
        //all valid bookings with endtime between now-11 min and now-10 min
        //alt1: for each scooter, get booking with endtime between (now-11 min) and (now-10 min)
        //date and endtime index: get bookings from today with endtime between (now-11 min) and (now-10 min)
    //see if there is a trip in position and journey for each boooking
    //add representation of trip to each booking object

    private final SecretCache cache = new SecretCache();


    public void doStuff(){
        final String secret = cache.getSecretString("");
        Booking booking = new Booking();
        List<Booking> endedBookings = null;
        try{
             endedBookings = booking.bookingsByEndTime();
             endedBookings.forEach((b)->logger.info("got booking that ended: " + b));
             logger.info("number of bookings ended: " + endedBookings.size());
        }catch(Exception e) {
            logger.info(e.getMessage());
        }
        logger.info();
    }
}
