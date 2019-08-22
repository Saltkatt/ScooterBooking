package com.wirelessiths;

import com.wirelessiths.dal.Booking;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
       /* MockBooking mock = new MockBooking();
        //mock.setBookingId("7");
        mock.setScooterId("3");
        mock.setUserId("2");
        mock.setStartTime(LocalDateTime.parse("2019-08-22T12:15:15.592"));
        mock.setEndTime(LocalDateTime.parse("2019-08-22T12:15:20.592"));


        //mock.save(mock);
        System.out.println("done");*/
        List<MockBooking> list = null;
        /*try{
            mock.save(mock);
            list = mock.list();

        }catch(Exception e){
            System.out.println(e.getMessage());

        }*/

        MockBooking mock = null;

        try {

            mock = new MockBooking().get("66e928ba-5b5f-4124-8409-e24f18849fba");

        }catch(Exception io) {
            System.out.println("hitta inte");
        }



        try {


            mock.setScooterId("42");
            mock.update(mock);
            list = mock.list();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }


        System.out.println("done");
        System.out.println(list.size());
        list.forEach(System.out::println);

    }
}
