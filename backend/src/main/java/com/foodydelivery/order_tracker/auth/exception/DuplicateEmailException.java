package com.foodydelivery.order_tracker.auth.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Email already registered");
    }
}
