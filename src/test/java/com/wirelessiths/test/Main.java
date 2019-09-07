package com.wirelessiths.test;

import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;

import java.time.*;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {

        Booking booking = new Booking();
        booking.setStartTime(Instant.now());
        booking.setEndTime(Instant.now().plusSeconds(60 * 60 *2));

        System.out.println(booking.getStartTime());
        System.out.println(booking.getEndTime());
        System.out.println(booking.getDate());
    }
}
