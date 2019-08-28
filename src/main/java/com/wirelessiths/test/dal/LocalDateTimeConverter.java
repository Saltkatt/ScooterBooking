package com.wirelessiths.test.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements DynamoDBTypeConverter<String, LocalDateTime> {

    @Override
    public String convert( final LocalDateTime time ) {

        return time.toString();
        //return time.
    }

    @Override
    public LocalDateTime unconvert( final String stringValue ) {

        return LocalDateTime.parse(stringValue);
    }
}
