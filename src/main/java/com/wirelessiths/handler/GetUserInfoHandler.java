package com.wirelessiths.handler;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.wirelessiths.ApiGatewayResponse;
import com.wirelessiths.Response;
import com.wirelessiths.dal.UserResponse;
import com.wirelessiths.exception.BookingDoesNotExistException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class handles get requests and implements RequestHandler and ApiGatewayResponse.
 */
public class GetUserInfoHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final Logger logger = LogManager.getLogger(this.getClass());


    /**
     * This method connects to the ApiGatewayResponse and request handler to allow the retrieval of individual bookings.
     * @param input contains path parameters that allow users to retrieve bookings connected to a bookingId.
     * @param context
     * @return
     */
    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        try {


            //String userPoolId = System.getenv("USER_POOL_ID");
            UserResponse userResponse = getUserInfo("adam2");
            /*

            List<UserPoolDescriptionType> userPools =
                    cognito.listUserPools(new ListUserPoolsRequest().withMaxResults(20)).getUserPools();


            ListUserPoolClientsResult response =
                    cognito.listUserPoolClients(
                            new ListUserPoolClientsRequest()
                                    .withUserPoolId(userPoolId)
                                    .withMaxResults(1)
                    );
            UserPoolClientType userPool =
                    cognito.describeUserPoolClient(
                            new DescribeUserPoolClientRequest()
                                    .withUserPoolId(userPoolId)
                                    .withClientId(
                                            response.getUserPoolClients().get(0).getClientId()
                                    )
                    ).getUserPoolClient();



            logger.info(userResponse.toString());


             */

            // send the response back
            if (userResponse != null) {
                return ApiGatewayResponse.builder()
                        .setStatusCode(200)
                        .setObjectBody(userResponse)
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            } else {
                return ApiGatewayResponse.builder()
                        .setStatusCode(404)
                        .setObjectBody("Booking with id: '" + userResponse + "' not found.")
                        .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                        .build();
            }

        } catch (BookingDoesNotExistException ex) {
            logger.error("Error in retrieving Booking as booking  is null: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Error in retrieving Booking: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();

        } catch (Exception ex) {
            logger.error("Error in retrieving Booking: " + ex);
            logger.error(ex.getMessage());
            ex.printStackTrace();

            // send the error response back
            Response responseBody = new Response("Unknown error in retrieving Booking: " + ex.getMessage(), input);
            return ApiGatewayResponse.builder()
                    .setStatusCode(500)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
                    .build();
        }

    }
    public UserResponse getUserInfo(String username) {

        String userPoolId = System.getenv("USER_POOL_ID");

        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.standard().withRegion(Regions.US_EAST_1).defaultClient();
        //AWSCognitoIdentityProvider cognitoClient = getAwsCognitoIdentityProvider();
        AdminGetUserRequest userRequest = new AdminGetUserRequest()
                .withUsername(username)
                .withUserPoolId(userPoolId);
        
        AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);

        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(userResult.getUsername());
        userResponse.setUserStatus(userResult.getUserStatus());
        userResponse.setUserCreateDate(userResult.getUserCreateDate());
        userResponse.setLastModifiedDate(userResult.getUserLastModifiedDate());
        

        List<AttributeType> userAttributes = userResult.getUserAttributes();

        
        for(AttributeType attribute : userAttributes) {
             if(attribute.getName().equals("email")) {
                userResponse.setEmail(attribute.getValue());
            }
        }
        cognitoClient.shutdown();
        return userResponse;

    }

    public AWSCognitoIdentityProvider getAwsCognitoIdentityProvider() {
        return AWSCognitoIdentityProviderClientBuilder.defaultClient();
    }





}
