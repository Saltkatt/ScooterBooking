package com.wirelessiths.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wirelessiths.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class AuthServiceTest {



   private Map<String, Object> mostOut = new HashMap<>();
   private Map<String, Object> middleOut = new HashMap<>();
   private Map<String, Object> middle = new HashMap<>();
   private Map<String, String> innerMost = new HashMap<>();
    private Map<String, List<String>> innerMostList = new HashMap<>();

    @Test
    public void yieldsInmostString() {

        innerMost.put("sub", "1234");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        String result = AuthService.getUserId(mostOut);
        assertEquals("1234", result);

    }

    @Test
    public void yieldsEmptyStringWhenInmostIsNull() {


        innerMost.put(null, null);
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        String result = AuthService.getUserId(mostOut);
        assertEquals("", result);

    }

    @Test
    public void yieldsEmptyStringWhenFieldInMiddleHashMapIsNull() {

        innerMost.put("sub", "1234");
        middle.put("claims", null);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        String result = AuthService.getUserId(mostOut);
        assertEquals("", result);

    }

    @Test
    public void yieldsEmptyStringWhenHashMapsThemselvesAreNull() {

        innerMost.put("sub", "1234");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        innerMost = null;
        middle = null;
        middleOut = null;
        mostOut = null;

        String result = AuthService.getUserId(mostOut);
        assertEquals("", result);
    }

    @Test
    public void isAdminReturnsTrueWhenAdmin() {

        innerMost.put("cognito:groups", "admin");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertTrue(result);

    }

    @Test
    public void isAdminReturnsFalseWhenUser() {

        innerMost.put("cognito:groups", "user");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertFalse(result);

    }

    @Test
    public void isAdminReturnsFalseWhenNull() {

        middle.put("claims", null);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertFalse(result);

    }

    @Test
    public void isAdminReturnsTrueWhenMultipleGroupsAndOneOfThemIsAdmin() {

        innerMost.put("cognito:groups", "[\"admin\", \"users\"]");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertTrue(result);

    }

    @Test
    public void isAdminFalseWhenNoAdminAdded() {

        innerMost.put("cognito:groups", "[\"benny\", \"users\"]");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertFalse(result);

    }




}