package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Comment Dto
 */

@Data
@AllArgsConstructor
public class CommentDto {
    private long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}