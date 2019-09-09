package com.wirelessiths.dal;

import com.wirelessiths.service.AuthService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> groups = new ArrayList<>();
        groups.add("admin");
        groups.add("user");
        innerMostList.put("cognito:groups", groups);
        middle.put("claims", innerMostList);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertTrue(result);

    }

    @Test
    public void isAdminFalseWhenNoAdminAdded() {
        List<String> groups = new ArrayList<>();
        groups.add("user2");
        groups.add("user");
        innerMostList.put("cognito:groups", groups);
        middle.put("claims", innerMostList);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertFalse(result);

    }

    @Test
    public void isAdminWhenOneAdminString() {

        innerMost.put("cognito:groups", "admin");
        middle.put("claims", innerMost);
        middleOut.put("authorizer", middle);
        mostOut.put("requestContext", middleOut);

        boolean result = AuthService.isAdmin(mostOut);
        assertTrue(result);

    }


}