package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Booking Dto.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}