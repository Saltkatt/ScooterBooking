package com.wirelessiths.test;

import com.wirelessiths.dal.Booking;
import com.wirelessiths.dal.TripStatus;

import java.time.*;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {

//        ZoneId stockholm = ZoneId.of("Europe/Stockholm");
//
//        Instant start = Instant.now().minusSeconds(60 * 10);
//        //Instant now = Instant.now(Clock.system(ZoneId.of("Asia/Seoul")));
//        Instant test = Instant.now(Clock.system(ZoneId.of("Europe/Stockholm")));
//        Instant test2 = Instant.now();


        LocalDateTime now = LocalDateTime.now();
        //Instant now2 = Instant.from(now);
        //Instant instant = now.atZone(ZoneId.of("Europe/Stockholm")).toInstant();
        //Instant instant1 = now.toInstant(ZoneOffset.ofHours(0));
        //ZoneOffset offset = ZoneOffset.UTC;
        Instant instant1 = now.toInstant(ZoneOffset.UTC);

        //ZoneId stockholm = ZoneId.of("Europe/Stockholm");
        //ZonedDateTime time = ZonedDateTime.ofInstant(now, stockholm);

        //System.out.println("start: " + start);
        System.out.println("now: " + now );
        //System.out.println("time: " + time);
        //System.out.println("test: " + test);

        System.out.println("localdatetime: " + now);
        System.out.println("instant1: " + instant1);
        //System.out.println(now.isAfter(start) && now.isBefore(start.plusSeconds(60 * 20)));

        System.out.println(TripStatus.WAITING_TO_START);
        Booking b = new Booking();
        b.setTripStatus(TripStatus.WAITING_TO_START);
        System.out.println(b.getTripStatus());
        System.out.println(b.getTripStatus().equals(TripStatus.WAITING_TO_START));
    }
}
