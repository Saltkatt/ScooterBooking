package com.wirelessiths.test.dal;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

    @Override
    public String convert( final LocalDate time ) {

        return time.toString();
        //return time.
    }

    @Override
    public LocalDate unconvert( final String stringValue ) {

        return LocalDate.parse(stringValue);
    }
}
