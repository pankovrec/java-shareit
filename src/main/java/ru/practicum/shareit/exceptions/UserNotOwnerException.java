package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * UserNotOwnerException
 */

public class UserNotOwnerException extends RuntimeException {
    public UserNotOwnerException(String message) {
        super(message);
    }
}
