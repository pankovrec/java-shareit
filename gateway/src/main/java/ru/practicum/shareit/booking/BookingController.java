package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Booking controller
 */

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody @DateValidator BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@PathVariable long bookingId,
                                               @RequestParam boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.changeStatus(bookingId, approved, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBooking(@RequestParam(defaultValue = "ALL") State state,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingClient.getAllBooking(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestParam(defaultValue = "ALL") State state,
                                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        return bookingClient.getAllBookingByOwner(userId, state, from, size);
    }
}