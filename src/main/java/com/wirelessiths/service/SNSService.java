package com.wirelessiths.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SNSService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    // Publish a message to an Amazon SNS topic.

    private String message;
    private Map<String, MessageAttributeValue> messageAttributes;

    public SNSService(final String message) {
        this.message = message;
        messageAttributes = new HashMap<>();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void addAttribute(final String attributeName, final String attributeValue) {
        final MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(attributeValue);
        messageAttributes.put(attributeName, messageAttributeValue);
    }

    public void addAttribute(final String attributeName, final ArrayList<?> attributeValues) {
        String valuesString, delimiter = ", ", prefix = "[", suffix = "]";
        if (attributeValues.get(0).getClass() == String.class) {
            delimiter = "\", \"";
            prefix = "[\"";
            suffix = "\"]";
        }
        valuesString = attributeValues
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter, prefix, suffix));
        final MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withDataType("String.Array")
                .withStringValue(valuesString);
        messageAttributes.put(attributeName, messageAttributeValue);
    }

    public void addAttribute(final String attributeName, final Number attributeValue) {
        final MessageAttributeValue messageAttributeValue = new MessageAttributeValue()
                .withDataType("Number")
                .withStringValue(attributeValue.toString());
        messageAttributes.put(attributeName, messageAttributeValue);
    }

    public String publish(final AmazonSNS snsClient, final String topicArn) {
        final PublishRequest request = new PublishRequest(topicArn, message)
                .withMessageAttributes(messageAttributes);
        final PublishResult result = snsClient.publish(request);
        return result.getMessageId();
    }

    /**
     * @param snsClient The client that should send the sms
     * @param message The message that you want to send
     * @param phoneNumber The phoneNumber. If the phone number is empty dont send any message.
     * @param smsAttributes Attributes for the message
     */
    public static void sendSMSMessage(AmazonSNSClient snsClient, String message,
                                      String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {
        if (!phoneNumber.isEmpty()) {
            PublishResult result = snsClient.publish(new PublishRequest()
                    .withMessage(message)
                    .withPhoneNumber(phoneNumber)
                    .withMessageAttributes(smsAttributes));
            logger.info(result);
        }
    }

    public static AmazonSNSClient getAmazonSNSClient() {
        return new AmazonSNSClient();
    }

}