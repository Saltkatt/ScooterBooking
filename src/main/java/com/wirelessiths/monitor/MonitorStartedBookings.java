package com.wirelessiths.monitor;

import com.wirelessiths.dal.Booking;

import java.util.List;

public class MonitorStartedBookings {

    //once a min, get all valid bookings with endTime between 6 and 5 mins ago
    //once a min, get all valid bookings with startTime between 16 and 15 mins ago

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

    }
}
