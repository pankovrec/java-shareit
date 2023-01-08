package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static ru.practicum.shareit.user.UserMapper.toUser;

/**
 * User service impl.
 */

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = toUser(userDto);
        User addedUser = userRepository.save(user);
        return UserMapper.toUserDto(addedUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto user) {
        User updatedUser = userCheck(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public UserDto getUser(long userId) {
        User user = userCheck(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDto(users);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private User userCheck(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователь с id = %d не найден!", userId)));
    }
}