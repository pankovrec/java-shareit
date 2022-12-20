package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * ItemRequest.
 */
@Data
public class ItemRequest {
    private long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
