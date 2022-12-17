package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    /**
     * добавление пользователя
     */
    UserDto createUser(UserDto user);

    /**
     * обновление пользователя
     */
    UserDto updateUser(long userId, UserDto user) throws NotFoundUserException;

    /**
     * полуяить пользователя по id
     */
    UserDto getUser(long userId) throws NotFoundUserException;

    /**
     * получить всех пользователей
     */
    Collection<UserDto> getAllUsers();

    /**
     * удаление пользователя
     */
    void deleteUser(long userId);
}