package ru.practicum.shareit.booking.dto;

import lombok.Data;

/**
 * Booking DtoItem.
 */

@Data
public class BookingDtoItem {
    private long id;
    private long bookerId;

    public BookingDtoItem(long id, long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
