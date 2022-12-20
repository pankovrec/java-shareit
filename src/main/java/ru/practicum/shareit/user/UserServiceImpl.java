package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto createUser(UserDto user) {
        return userStorage.addUser(UserMapper.toUser(user));
    }

    @Override
    public UserDto updateUser(long userId, UserDto user) {
        if (!userStorage.getAllUsers().contains(userStorage.getUser(userId))) {
            log.info("Нет пользователя с id {}", userId);
            throw new NotFoundUserException("Нет пользователя с id " + userId);
        }
        return userStorage.updateUser(userId, UserMapper.toUser(user));
    }

    @Override
    public UserDto getUser(long userId) {
        if (!userStorage.getAllUsers().contains(userStorage.getUser(userId))) {
            log.info("Нет пользователя с id {}", userId);
            throw new NotFoundUserException("Нет пользователя с id " + userId);
        }
        return userStorage.getUser(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.deleteUser(userId);
    }
}