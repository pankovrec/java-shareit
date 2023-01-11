package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

/**
 * OutBookingDto.
 */

@Data
@AllArgsConstructor
public class OutBookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private Status status;


    @AllArgsConstructor
    @Getter
    public static class Item {
        private long id;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class User {
        private long id;
    }
}