package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * UserModel.
 */
@Data
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    @NotNull
    @Email
    private String email;

    public User() {
    }
}
