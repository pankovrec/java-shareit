package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * NotAvailableBookingException
 */

public class NotAvailableBookingException extends RuntimeException {

    public NotAvailableBookingException(String message) {
        super(message);
    }
}
