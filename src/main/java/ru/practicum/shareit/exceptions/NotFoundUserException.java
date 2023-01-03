package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * NotFoundUserException
 */

public class NotFoundUserException extends RuntimeException {

    public NotFoundUserException(String message) {
        super(message);
    }
}