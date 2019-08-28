package com.wirelessiths.test.exception;

public class UnableToListBookingsException extends IllegalStateException {
    public UnableToListBookingsException(String message) {
        super(message);
    }
}
