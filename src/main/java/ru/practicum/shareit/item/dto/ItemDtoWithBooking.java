package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Item Dto withBooking
 */

@Data
@AllArgsConstructor
public class ItemDtoWithBooking extends ItemDto {
    private long id;
    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private Set<CommentDto> comments;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class CommentDto {
        private long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}

