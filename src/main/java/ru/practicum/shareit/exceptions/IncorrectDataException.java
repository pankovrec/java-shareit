package ru.practicum.shareit.exceptions;

/**
 * CustomException
 * IncorrectDataException
 */

public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String message) {
        super(message);
    }
}
