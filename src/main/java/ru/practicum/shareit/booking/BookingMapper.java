package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

/**
 * BookingMapper
 */

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd());
    }

    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        return new BookingDtoItem(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static OutBookingDto toBookingDto(Booking booking) {
        return new OutBookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem() != null ? ItemMapper.toItemDto(booking.getItem()) : null,
                booking.getBooker() != null ? UserMapper.toUserDto(booking.getBooker()) : null,
                booking.getStatus() != null ? booking.getStatus() : null);
    }
}