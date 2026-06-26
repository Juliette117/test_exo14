package org.example.mediacity.exception;

public class DuplicateReservationException extends RuntimeException {
    public DuplicateReservationException(String message) {
        super(message);
    }
}
