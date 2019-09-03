package com.wirelessiths.test;

public class Main {

    public static void main(String[] args) {
<<<<<<< HEAD
<<<<<<< HEAD
=======


        Instant instant = Instant.now();
        System.out.println(instant);
        String [] t = instant.toString().split("T");
        for (String s: t) {
            System.out.println(s);

        }
//        String scooterId = "123";
//        LocalDate date = LocalDate.now();
//        LocalTime start = LocalTime.parse("14:10:00");
//        LocalTime end = LocalTime.parse("14:30:00");
//        Instant instant = Instant.now();
//        System.out.println(start);
//        LocalDate date2 = LocalDate.now();
//
//        System.out.println(date2);
//
//        //System.out.println(instant);
////        System.out.println("localDate: " + date);
////        System.out.println("startTime: " + start);
////        System.out.println("endTime: " + end);
////        System.out.println("ScooterId: " + scooterId);
////        System.out.println(scooterId + "-" + start + "-" + end);
//
//
//        //====================================================================
//        //POPULATE DATABASE
//        //====================================================================
//        MockBooking mock = new MockBooking(date, scooterId, start, end);
//        mock.setUserId("carl");
//
//        // date = LocalDate.now();
////        start = LocalTime.parse("14:00:00");
////        end = LocalTime.parse("15:00:00");
////        MockBooking mock2 = new MockBooking(date, scooterId, start, end);
////        mock2.setUserId("sven");
////
////        start = LocalTime.parse("15:30:00");
////        end = LocalTime.parse("16:00:00");
////        MockBooking mock3 = new MockBooking(date, scooterId, start, end);
////        mock3.setUserId("robin");
////
////        scooterId = "456";
////        start = LocalTime.parse("15:30:00");
////        end = LocalTime.parse("16:00:00");
////        MockBooking mock5 = new MockBooking(date, scooterId, start, end);
////        mock5.setUserId("sven");
////
////        scooterId = "123";
////        start = LocalTime.parse("16:10:00");
////        end = LocalTime.parse("17:00:00");
////        MockBooking mock4 = new MockBooking(date, scooterId, start, end);
////        mock4.setUserId("elin");
////
////        System.out.println("booking: " +mock);
////        try{
////            mock.save(mock);
////            mock2.save(mock2);
////            mock3.save(mock3);
////            mock4.save(mock4);
////            mock5.save(mock5);
////        }catch(Exception e){
////            System.out.println(e.getMessage());
////        }
//
//
//        mock.validateBooking(mock);
//
//        //mock.setDate();
//
//
//       /* MockBooking mock = new MockBooking();
//        //mock.setBookingId("7");
//        mock.setScooterId("3");
//        mock.setUserId("2");
//        mock.setStartTime(LocalDateTime.parse("2019-08-22T12:15:15.592"));
//        mock.setEndTime(LocalDateTime.parse("2019-08-22T12:15:20.592"));
//
//
//        //mock.save(mock);
//        System.out.println("done");*/
//        List<MockBooking> list = null;
//        /*try{
//            mock.save(mock);
//            list = mock.list();
//
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//
//        }*/
//
////        MockBooking mock = null;
////
////        try {
////
////            mock = new MockBooking().get("66e928ba-5b5f-4124-8409-e24f18849fba");
////
////        }catch(Exception io) {
////            System.out.println("hitta inte");
////        }
//
//
//
//        try {
//
//
//            mock.setScooterId("42");
//            mock.update(mock);
//            list = mock.list();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//
//
//        System.out.println("done");
//        System.out.println(list.size());
//        list.forEach(System.out::println);

>>>>>>> add working booking validation logic
=======
>>>>>>> rename getter, remove unused code
    }
}
