package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Booking DtoItem.
 */

@Data

@NoArgsConstructor

public class BookingDtoItem {
    private long id;
    private long bookerId;

    public BookingDtoItem(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
