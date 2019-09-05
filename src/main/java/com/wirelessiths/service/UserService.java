package com.wirelessiths.service;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.wirelessiths.dal.User;

public class UserService {

    public static User convertCognitoUser(UserType awsCognitoUser) {

        User.UserBuilder builder = User.builder();

        for (AttributeType userAttribute : awsCognitoUser.getAttributes()) {
            switch (userAttribute.getName()) {
                case "sub":
                    builder.sub(userAttribute.getValue());
                    break;
                case "given_name":
                    builder.firstName(userAttribute.getValue());
                    break;
                case  "family_name":
                    builder.lastName(userAttribute.getValue());
                    break;
                case "username":
                    builder.username(userAttribute.getValue());
                    break;
                case "email":
                    builder.email(userAttribute.getValue());
                    break;
                case "phone_number":
                    builder.phoneNumber(userAttribute.getValue());
                    break;
                case "preferred_username":
                    builder.preferred_username(userAttribute.getValue());
                    break;
                case "cognito:user_status":
                    builder.cognitoUserStatus(userAttribute.getValue());
                    break;
                case "status":
                    builder.userStatus(userAttribute.getValue());
                    break;
            }
        }

        return builder.build();
    }
}
