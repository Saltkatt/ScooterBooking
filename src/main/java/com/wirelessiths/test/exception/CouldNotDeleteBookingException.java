package com.wirelessiths.test.exception;

public class CouldNotDeleteBookingException extends IllegalStateException {
    public CouldNotDeleteBookingException(String message) {
        super(message);
    }

}
