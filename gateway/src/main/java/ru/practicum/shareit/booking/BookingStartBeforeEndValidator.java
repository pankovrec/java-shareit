package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStartBeforeEndValidator implements ConstraintValidator<DateValidator, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            return false;
        } else {
            return true;
        }
    }
}
