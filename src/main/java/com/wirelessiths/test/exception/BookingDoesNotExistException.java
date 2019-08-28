package com.wirelessiths.test.exception;

public class BookingDoesNotExistException extends IllegalArgumentException {

    public BookingDoesNotExistException(String message) {
        super(message);
    }
}
