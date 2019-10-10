package com.wirelessiths.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    /**
     * Looks in nested request Map for user claims. "sub" as input for userId, "username" as input for username.
     * @param input The request from lambda
     * @return corresponding value from claims map, if none found return ""
     * The input Map is always constructed in this way, therefore unchecked class cast is suppressed.
     */
    @SuppressWarnings("unchecked")
    public static String getUserId(Map<String, Object> input) {
        return Optional.ofNullable(input)
                .map(m -> (Map<String, Map>)m.get(("requestContext")))
                .map(m -> (Map<String, Map>)m.get("authorizer"))
                .map(m -> (Map<String, String>)m.get("claims"))
                .map(m -> m.get("sub")).orElse("sam-bot");

    }

    /**
     * Checks in request if user has admin privileges
     * @param input The request from a lambda
     * @return True if admin, false if not admin
     * The input Map is always constructed in this way, therefore unchecked class cast is suppressed.
     */
    @SuppressWarnings("unchecked")
    public static boolean isAdmin(Map<String, Object> input) {
        String cognitoGroups = Optional.ofNullable(input).map(m -> (Map<String, Map>)m.get(("requestContext"))).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, String>)m.get("claims")).map(m -> m.get("cognito:groups")).orElse("");

        if (cognitoGroups.startsWith("[") && cognitoGroups.endsWith("]")) {
            ObjectMapper mapper = new ObjectMapper();
            List<String> groupsList = new ArrayList<>();
            try {
                groupsList = Arrays.asList(mapper.readValue(cognitoGroups, String[].class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String s: groupsList) {
                if(s.equals("admin")) {
                    return true;
                }
            }
            return false;
        } else if (cognitoGroups.contains(",")){
            List<String> items = Arrays.asList(cognitoGroups.split("\\s*,\\s*"));
            for (String s: items) {
                if(s.equals("admin")) {
                    return true;
                }
            }
        }
        return cognitoGroups.equals("admin");
    }


    public static boolean isAuthorized(boolean isAdmin, String queryUserId, String tokenUserId){
        return isAdmin || queryUserId.equals(tokenUserId);
    }
}
