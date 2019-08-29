package com.wirelessiths.dal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    /**
     * Looks in nested request Map for user claims. "sub" as input for userId, "username" as input for username.
     * @param input The request from a lambda
     * @param field The key in the claims map
     * @return corresponding value from claims map, if none found return ""
     */
    public static String getUserInfo(Map<String, Object> input, String field) {
        return Optional.ofNullable((Map<String, Map>)input.get("requestContext")).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, String>)m.get("claims")).map(m -> m.get(field)).orElse("");
    }

    public static boolean isAdmin(Map<String, Object> input) {
        return getUserInfo(input, "cognito:groups").equals("admin");
    }


}
