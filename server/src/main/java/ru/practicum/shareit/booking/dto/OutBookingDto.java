package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * OutBookingDto.
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OutBookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;

    @AllArgsConstructor
    @ToString
    @Getter
    public static class Item {
        private long id;
        private String name;
    }

    @AllArgsConstructor
    @ToString
    @Getter
    public static class User {
        private long id;
    }
}