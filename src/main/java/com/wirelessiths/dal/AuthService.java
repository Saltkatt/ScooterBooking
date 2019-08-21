package com.wirelessiths.dal;

import java.util.*;

public class AuthService {
    /**
     * Looks in nested request Map for user claims.
     * @param input The request from a lambda
     * @param field The param in the claims map
     * @return param from claims map, if none found return ""
     */
    public static String getUserInfo(Map<String, Object> input, String field) {
        return Optional.ofNullable((Map<String, Map>)input.get("requestContext")).map(m -> (Map<String, Map>)m.get("authorizer")).map(m -> (Map<String, String>)m.get("claims")).map(m -> m.get(field)).orElse("");
    }

}
