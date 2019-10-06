import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SNSService {

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


    static void sendSMSMessage(AmazonSNS snsClient, String message,
                               String phoneNumber, Map<String, MessageAttributeValue> smsAttributes) {

        phoneNumber = System.getenv("phoneNumber");

        if (!phoneNumber.isEmpty()) {

            PublishResult result = snsClient.publish(new PublishRequest()

                    .withMessage(message)

                    .withPhoneNumber(phoneNumber)

                    .withMessageAttributes(smsAttributes));


        }

    }


    static AmazonSNS getAmazonSNSClient() {

        return AmazonSNSClient.builder().withRegion(Regions.US_EAST_1).build();

    }

}