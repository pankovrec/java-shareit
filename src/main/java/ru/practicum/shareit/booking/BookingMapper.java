package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;

/**
 * BookingMapper
 */

public class BookingMapper {
    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        return new BookingDtoItem(
                booking.getId(), booking.getBooker().getId());
    }

    public static OutBookingDto toBookingDto(Booking booking) {
        return new OutBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                new OutBookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new OutBookingDto.User(booking.getBooker().getId()),
                booking.getStatus() != null ? booking.getStatus() : null);
    }
}