package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingDtoItemTest {
    @Test
    void testConstructor() {
        User user = new User(1L, "user1", "user1@mail.ru");
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(user);
       BookingDtoItem bookingDtoItem = BookingMapper.toBookingDtoItem(booking);

        assertEquals(booking.getId(), bookingDtoItem.getId());
        assertEquals(booking.getBooker().getId(), bookingDtoItem.getBookerId());
    }
}
