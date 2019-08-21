package com.wirelessiths.dal;

import java.util.*;

public class AuthService {

    public static String getUserInfo(Map<String, Object> input, String field) {

        Optional<Map<String, Object>> opt = Optional.ofNullable((Map<String, Object>) input.get("requestContext"));

         Map<String, Object> requestContext = ((Map<String, Object>) input.get("requestContext"));
         Map<String, Object> authorizer = (Map<String, Object>) requestContext.get("authorizer");
         Map<String, Object> claims = (Map<String, Object>) authorizer.get("claims");
        return (String) claims.get("sub");

    }

}
