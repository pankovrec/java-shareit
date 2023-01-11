package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import javax.validation.Valid;
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
    private OutBookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    private OutBookingDto getBooking(@PathVariable long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    private OutBookingDto changeBookingStatus(@PathVariable long bookingId,
                                              @RequestParam boolean approved,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.updateBookingStatus(bookingId, approved, userId);
    }

    @GetMapping
    private List<OutBookingDto> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    private List<OutBookingDto> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getAllBookingByOwner(userId, state);
    }
}