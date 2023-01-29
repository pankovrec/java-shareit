package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import java.util.List;

/**
 * Booking controller
 */

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public OutBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public OutBookingDto getBooking(@PathVariable long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutBookingDto changeBookingStatus(@PathVariable long bookingId,
                                             @RequestParam boolean approved,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping
    public List<OutBookingDto> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<OutBookingDto> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingByOwner(userId, state, from, size);
    }
}