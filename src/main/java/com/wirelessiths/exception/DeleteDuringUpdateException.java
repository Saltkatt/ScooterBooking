package com.wirelessiths.exception;

public class DeleteDuringUpdateException extends IllegalStateException {

    public DeleteDuringUpdateException(String message) {
        super(message);
    }
}
