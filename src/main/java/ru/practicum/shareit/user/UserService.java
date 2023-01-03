package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    /**
     * добавление пользователя
     */
    UserDto createUser(UserDto userDto);

    /**
     * обновление пользователя
     */
    UserDto updateUser(long userId, UserDto user);

    /**
     * получить пользователя по id
     */
    UserDto getUser(long userId);

    /**
     * получить всех пользователей
     */
    List<UserDto> getAllUsers();

    /**
     * удаление пользователя
     */
    void deleteUser(long userId);
}