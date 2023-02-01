package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.DateValidator;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Booking Dto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@DateValidator
public class BookingDto {
    @NotNull
    private long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}