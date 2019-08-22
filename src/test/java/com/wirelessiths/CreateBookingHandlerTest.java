package com.wirelessiths;

import com.wirelessiths.dal.Booking;

import java.io.IOException;
import java.time.LocalDateTime;

public class CreateBookingHandlerTest {

    public void requestHandler(){
        Booking booking = new Booking();

        booking.setScooterId("123");
        booking.setUserId("1");
        booking.setStartTime(LocalDateTime.parse("2019-08-22T12:15:20.592"));
        booking.setEndTime(LocalDateTime.parse("2019-08-22T12:15:25.592"));

        try{
            booking.save(booking);
        }catch(IOException e){

        }
    }
}
