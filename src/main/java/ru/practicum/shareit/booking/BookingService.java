package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;

import java.util.List;

/**
 * BookingService
 */
public interface BookingService {

    /**
     * забронировать
     */
    OutBookingDto createBooking(long userId, BookingDto bookingDto);

    /**
     * получить бронирование по id
     */
    OutBookingDto getBooking(long bookingId, long userId);

    /**
     * обновленить статус брони
     */
    OutBookingDto updateBookingStatus(long bookingId, boolean approved, long userId);

    /**
     * получить все брони пользователя
     */
    List<OutBookingDto> getAllBookingByUser(long userId, State state);

    /**
     * получить все брони владельца
     */
    List<OutBookingDto> getAllBookingByOwner(long userId, State state);
}