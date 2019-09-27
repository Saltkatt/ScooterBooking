package com.wirelessiths.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.wirelessiths.dal.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

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

    public static List<User> listUsers(String sub, String userPoolId) {

        AWSCognitoIdentityProvider cognitoClient = UserService.getAwsCognitoIdentityProvider();


        ListUsersRequest listUsersRequest = new ListUsersRequest().withFilter("sub = \"" + sub + "\"").withUserPoolId(userPoolId);
        ListUsersResult userResults = cognitoClient.listUsers(listUsersRequest);

        List<UserType> userTypeList = userResults.getUsers();
        List<User> users = userTypeList.stream().map(UserService::convertCognitoUser).collect(Collectors.toList());

        while (userResults.getPaginationToken() != null) {
            try {
                listUsersRequest.setPaginationToken(userResults.getPaginationToken());
                userResults = cognitoClient.listUsers(listUsersRequest);

                users.addAll(userTypeList.stream().map(UserService::convertCognitoUser).collect(Collectors.toList()));
            } catch (TooManyRequestsException e) {
                // cognito hard rate limit for "list users": 5 per second. */
                try {
                    logger.warn("Too many requests", e);
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    logger.warn("Error while sleeping", e);
                }
            }
        }
        return users;
    }

    /**
     * @param userId is the UUID that we use to fetch the user from Cognito
     * @param userPoolId the userPool where the user is
     * @return if user found and has phone number, returns phone number as String. Else return empty string.
     */
    public static String getUserPhoneNumber(String userId, String userPoolId) {
        List<User> users = listUsers(userId, userPoolId);
        if (!users.isEmpty()){
           return Optional.ofNullable(users.get(0).getPhoneNumber()).orElse("");
        }
        else {
            return "";
        }

    }

    /**
     * @param userId is the UUID that we use to fetch the user from Cognito
     * @param userPoolId the userPool where the user is
     * @return if user found and have an email, returns email as String. Else return empty string.
     */
    public static String getUserEmail(String userId, String userPoolId) {
        List<User> users = listUsers(userId, userPoolId);
        if (!users.isEmpty()){
            return Optional.ofNullable(users.get(0).getEmail()).orElse("");
        }
        else {
            return "";
        }

    }


    /**
     *
     * @return defaultclient with EU_WEST_1 config
     */

    public static AWSCognitoIdentityProvider getAwsCognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder.standard().withRegion(Regions.EU_WEST_1).defaultClient();
    }

    /**
     *
     * @param username UserName in the cognitoPool
     * @param userPoolId The pool id
     * @return User object with fields set from user in cognito.
     */
    public static User getUserInfo(String username, String userPoolId) {

        AWSCognitoIdentityProvider cognitoClient = getAwsCognitoIdentityProvider();

        AdminGetUserRequest userRequest = new AdminGetUserRequest()
                .withUsername(username)
                .withUserPoolId(userPoolId);

        AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);

            User user = new User();
            user.setUsername(userResult.getUsername());
            user.setUserStatus(userResult.getUserStatus());
            user.setUserCreateDate(userResult.getUserCreateDate());
            user.setLastModifiedDate(userResult.getUserLastModifiedDate());

            List<AttributeType> userAttributes = userResult.getUserAttributes();

            for (AttributeType attribute : userAttributes) {
                if (attribute.getName().equals("email")) {
                    user.setEmail(attribute.getValue());
                }
                else if(attribute.getName().equals("phone_number")) {
                    user.setPhoneNumber(attribute.getValue());
                }
            }
            cognitoClient.shutdown();
            return user;

        }

        public static void deleteUser(String username, String userPoolId) {

            AWSCognitoIdentityProvider cognitoClient = getAwsCognitoIdentityProvider();
            AdminDeleteUserRequest adminDeleteUserRequest = new AdminDeleteUserRequest();
            adminDeleteUserRequest.withUsername(username).withUserPoolId(userPoolId);
            cognitoClient.adminDeleteUser(adminDeleteUserRequest);
        }
}
