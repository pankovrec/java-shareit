package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Comment Dto fromRequest
 */

@Data
public class CommentDtoFromRequest {
    @NotNull
    @NotEmpty
    private String text;
}