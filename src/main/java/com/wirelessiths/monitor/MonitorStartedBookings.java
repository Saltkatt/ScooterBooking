package com.wirelessiths.monitor;

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

    }
}
