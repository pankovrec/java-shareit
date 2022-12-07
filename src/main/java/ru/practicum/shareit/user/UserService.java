package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    User updateUser(long userId, User user);

    UserDto getUser(long userId);

    Collection<UserDto> getAllUsers();

    void deleteUser(long userId);
}