package com.wirelessiths.dal;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class AuthServiceTest {

    Map<String, Map<String, Map<String, Map<String, String>>>> mostOut = new HashMap<>();
    Map<String, Map<String, Map<String, String>>> middleOut = new HashMap<>();
    Map<String, Map<String, String>> middle = new HashMap<>();
    Map<String, String> innerMost = new HashMap<>();

    @Test
    public void yieldsInmostString() {

        innerMost.put("one", "two");
        middle.put("three", innerMost);
        middleOut.put("four", middle);
        mostOut.put("five", middleOut);

        String result = Optional.ofNullable(mostOut.get("five")).map(m -> m.get("four")).map(m -> m.get("three")).map(m -> m.get("one")).orElse("");
        assertEquals("two", result);

    }

    @Test
    public void yieldsEmptyStringWhenInmostIsNUll() {

        innerMost.put(null, null);
        middle.put("three", innerMost);
        middleOut.put("four", middle);
        mostOut.put("five", middleOut);

        String result = Optional.ofNullable(mostOut.get("five")).map(m -> m.get("four")).map(m -> m.get("three")).map(m -> m.get("one")).orElse("");
        assertEquals("", result);

    }
}