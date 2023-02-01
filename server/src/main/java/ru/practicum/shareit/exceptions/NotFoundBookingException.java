package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * NotFoundBookingException
 */

public class NotFoundBookingException extends RuntimeException {

    public NotFoundBookingException(String message) {
        super(message);
    }
}
