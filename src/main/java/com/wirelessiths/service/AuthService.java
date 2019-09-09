package com.wirelessiths.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    /**
     * Looks in nested request Map for user claims. "sub" as input for userId, "username" as input for username.
     * @param input The request from lambda
     * @return corresponding value from claims map, if none found return ""
     */
    public static String getUserId(Map<String, Object> input) {
        return Optional.ofNullable(input).map(m -> (Map<String, Map>)m.get(("requestContext"))).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, String>)m.get("claims")).map(m -> m.get("sub")).orElse("");

    }

    /**
     * Checks in request if user has admin privileges
     * @param input The request from a lambda
     * @return True if admin, false if not admin
     */
    public static boolean isAdmin(Map<String, Object> input) {
        Object cognitoGroups = Optional.ofNullable(input).map(m -> (Map<String, Map>)m.get(("requestContext"))).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, Object>)m.get("claims")).map(m -> m.get("cognito:groups")).orElse("");
         if(cognitoGroups instanceof String) {
            return cognitoGroups.equals("admin");
        } else if(cognitoGroups instanceof List){
            for (String s : (List<String>)cognitoGroups) {
                if (s.equals("admin")){
                    return true;
                }
            }
            return false;
        }
        return false;
    }


}
