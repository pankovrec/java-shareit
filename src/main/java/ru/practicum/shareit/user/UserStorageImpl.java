package ru.practicum.shareit.user;

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
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User addUser(User user) {
        if (validateEmail(user)) {
            throw new DuplicateEmailException(
                    String.format("Нельзя использовать повторяющиеся e-mail %s",
                            user.getEmail()));
        }
        user.setId(makeId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(long userId, User user) {
        User newUser = new User();
        if (users.containsKey(userId)) {
            newUser = users.get(userId);
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (validateEmail(user)) {
                throw new DuplicateEmailException(
                        String.format("e-mail %s не может повторяться",
                                user.getEmail()));
            }
            newUser.setEmail(user.getEmail());
        }
        users.put(userId, newUser);
        return newUser;
    }

    @Override
    public UserDto getUser(long userId) {
        if (users.containsKey(userId)) {
            return UserMapper.toUserDto(users.get(userId));
        } else {
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
        return (id == 0L) ? id = 1L : ++id;
    }
}