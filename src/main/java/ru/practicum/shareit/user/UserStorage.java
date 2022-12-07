package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {

    /**
     * добавление пользователя
     */
    User addUser(User user);

    /**
     * обновление пользователя
     */
    User updateUser(long userId, User user);

    /**
     * получить пользователя по id
     */
    UserDto getUser(long userId);

    /**
     * удаление пользователя
     */
    void deleteUser(long userId);

    /**
     * получить всех пользователей
     */
    Collection<UserDto> getAllUsers();
}