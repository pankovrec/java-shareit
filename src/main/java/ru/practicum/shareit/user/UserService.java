package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(UserDto user);

    User updateUser(long userId, UserDto user);

    UserDto getUser(long userId);

    Collection<UserDto> getAllUsers();

    void deleteUser(long userId);
}