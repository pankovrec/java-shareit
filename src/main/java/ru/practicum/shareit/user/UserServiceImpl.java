package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(UserDto user) {
        return userStorage.addUser(UserMapper.toUser(user));
    }

    @Override
    public User updateUser(long userId, UserDto user) {
        return userStorage.updateUser(userId, UserMapper.toUser(user));
    }

    @Override
    public UserDto getUser(long userId) {
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