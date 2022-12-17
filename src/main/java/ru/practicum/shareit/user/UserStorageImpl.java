package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public UserDto addUser(User user) {
        if (validateEmail(user)) {
            log.info("Нельзя использовать повторяющиеся email {}", user.getEmail());
            throw new DuplicateEmailException(
                    String.format("Нельзя использовать повторяющиеся e-mail %s", user.getEmail()));
        }
        user.setId(makeId());
        users.put(user.getId(), user);
        return UserMapper.toUserDto(users.get(user.getId()));
    }

    @Override
    public UserDto updateUser(long userId, User user) {
        User newUser = new User();
        if (users.containsKey(userId)) {
            newUser = users.get(userId);
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (validateEmail(user)) {
                log.info("Нельзя использовать повторяющиеся email {}", user.getEmail());
                throw new DuplicateEmailException(String.format("e-mail %s не может повторяться", user.getEmail()));
            }
            newUser.setEmail(user.getEmail());
        }
        users.put(userId, newUser);
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto getUser(long userId) {
        if (users.containsKey(userId)) {
            return UserMapper.toUserDto(users.get(userId));
        } else {
            log.info("Пользователя с id {} нет в системе", userId);
            throw new NotFoundUserException(String.format("Пользователя с id %s нет в системе", userId));
        }
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private boolean validateEmail(User user) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(user.getEmail()::equals);
    }

    private Long makeId() {
        return ++id;
    }
}