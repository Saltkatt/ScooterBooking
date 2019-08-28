package com.wirelessiths.test.dal;

import java.util.*;

public class AuthService {
    /**
     * Looks in nested request Map for user claims. "sub" as input for userId, "username" as input for username.
     * @param input The request from a lambda
     * @param field The key in the claims map
     * @return corresponding value from claims map, if none found return ""
     */
    public static String getUserInfo(Map<String, Object> input, String field) {
        return Optional.ofNullable((Map<String, Map>)input.get("requestContext")).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, String>)m.get("claims")).map(m -> m.get(field)).orElse("");
    }

}
