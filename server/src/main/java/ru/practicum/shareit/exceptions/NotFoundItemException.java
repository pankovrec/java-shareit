package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * NotFoundItemException
 */

public class NotFoundItemException extends RuntimeException {

    public NotFoundItemException(String message) {
        super(message);
    }
}