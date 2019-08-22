package com.wirelessiths;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        MockBooking mock = new MockBooking();
        //mock.setBookingId("7");
        mock.setScooterId("3");
        mock.setUserId("2");
        mock.setStartTime(LocalDateTime.parse("2019-08-22T12:15:15.592"));
        mock.setEndTime(LocalDateTime.parse("2019-08-22T12:15:20.592"));


        //mock.save(mock);
        System.out.println("done");
        List<MockBooking> list = null;
        try{
            mock.save(mock);
            list = mock.list();

        }catch(Exception e){
            System.out.println(e.getMessage());

        }
        System.out.println("done");
        System.out.println(list.size());
        list.forEach(System.out::println);

    }
}
