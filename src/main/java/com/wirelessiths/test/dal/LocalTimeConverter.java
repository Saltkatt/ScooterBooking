package com.wirelessiths.test.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalTime;

public class LocalTimeConverter implements DynamoDBTypeConverter<String, LocalTime> {

    @Override
    public String convert( final LocalTime time ) {

        return time.toString();
        //return time.
    }

    @Override
    public LocalTime unconvert(final String stringValue ) {

        return LocalTime.parse(stringValue);//LocalTimeConverter.parse(stringValue);
    }
}
