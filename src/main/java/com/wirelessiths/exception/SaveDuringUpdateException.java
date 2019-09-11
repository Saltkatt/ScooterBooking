package com.wirelessiths.exception;

public class SaveDuringUpdateException extends IllegalStateException {

    public SaveDuringUpdateException(String message) {
        super(message);
    }
}
