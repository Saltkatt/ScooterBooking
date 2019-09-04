package com.wirelessiths.test;

import java.time.Instant;

public class Main {

    public static void main(String[] args) {
        Instant start = Instant.now().minusSeconds(60 * 10);
        Instant now = Instant.now();

        System.out.println("start: " + start);
        System.out.println("now: " + now );
        System.out.println(now.isAfter(start) && now.isBefore(start.plusSeconds(60 * 20)));
    }
}
