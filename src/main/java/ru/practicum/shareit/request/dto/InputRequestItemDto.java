package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * ItemRequestItemDto.
 */

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InputRequestItemDto {
    @NotNull
    private String description;
}