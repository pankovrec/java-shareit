package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorHandler
 */

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Error> notFoundUser(NotFoundUserException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> notFoundItemException(NotFoundItemException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchNotAvailableBookingException(NotAvailableBookingException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> catchMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", String.format("Unknown %s: %s", e.getName(), e.getValue()));
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> catchNotFoundBookingException(NotFoundBookingException e) {
        return new ResponseEntity<>(new Error(HttpStatus.NOT_FOUND.value(), e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Error> incorrectDataHandler(final IncorrectDataException e) {
        return new ResponseEntity<>(new Error(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Error dataIntegrityViolationExceptionHandler(final DataIntegrityViolationException e) {
        return new Error(409, "Conflict" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Error logicExceptionHandler(final UserNotOwnerException e) {
        return new Error(404, "Not Found" + e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Error handleThrowable(final Throwable e) {
        return new Error(400,
                "Bad Request" + e.getMessage());
    }
}