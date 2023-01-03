package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Item Dto withBooking
 */

@Data
public class ItemDtoWithBooking {
    private long id;
    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private long requestId;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private Set<CommentDto> comments = new HashSet<>();

    public ItemDtoWithBooking(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
