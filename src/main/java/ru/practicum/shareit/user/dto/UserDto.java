package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * UserDto.
 */
@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    private String name;
    @NotNull
    @Email
    private String email;
}