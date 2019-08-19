package com.wirelessiths.exception;

public class BookingDoesNotExistException extends IllegalArgumentException {
    public BookingDoesNotExistException(String message) {
        super(message);
    }
}
