package ru.practicum.shareit.exceptions;

public class NotFoundItemException extends RuntimeException {

    public NotFoundItemException(String message) {
        super(message);
    }
}