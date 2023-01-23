package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exceptions.IncorrectDataException;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class UserServiceImplTest {
    UserService userService;
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    User user = new User(1, "user1", "user1@mail.ru");
    UserDto userDto = new UserDto(1, "user1", "user1@mail.ru");
    List<User> users = List.of(new User(1, "user1", "user1@mail.ru"),
            new User(2, "user2", "user2@mail.ru"),
            new User(3, "user3", "user3@mail.ru"));

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        UserDto foundedUserDto = userService.createUser(userDto);
        assertThat(foundedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(foundedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(foundedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        UserDto foundedUserDto = userService.getUser(1);
        assertThat(foundedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(foundedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(foundedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getByIdFail() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Пользователь с данным id не найден"));
        final NotFoundUserException exception = Assertions.assertThrows(NotFoundUserException.class,
                () -> userService.getUser(2));
        Assertions.assertEquals("Пользователь с данным id не найден", exception.getMessage());
    }

    @Test
    void getAll() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Collection<UserDto> userDtoList = userService.getAllUsers();
        assertThat(users.size(), equalTo(userDtoList.size()));
        for (UserDto foundedUserDto : userDtoList) {
            if (foundedUserDto.getId() == (1)) {
                assertThat(foundedUserDto.getId(), equalTo(1L));
                assertThat(foundedUserDto.getName(), equalTo("user1"));
                assertThat(foundedUserDto.getEmail(), equalTo("user1@mail.ru"));
            } else if (foundedUserDto.getId() == (2)) {
                assertThat(foundedUserDto.getId(), equalTo(2L));
                assertThat(foundedUserDto.getName(), equalTo("user2"));
                assertThat(foundedUserDto.getEmail(), equalTo("user2@mail.ru"));
            } else if (foundedUserDto.getId() == (3)) {
                assertThat(foundedUserDto.getId(), equalTo(3L));
                assertThat(foundedUserDto.getName(), equalTo("user3"));
                assertThat(foundedUserDto.getEmail(), equalTo("user3@mail.ru"));
            }
        }
    }

    @Test
    void update() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);
        UserDto foundedUserDto = userService.updateUser(1, userDto);
        assertThat(foundedUserDto.getId(), equalTo(userDto.getId()));
        assertThat(foundedUserDto.getName(), equalTo(userDto.getName()));
        assertThat(foundedUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateFail() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new IncorrectDataException("Почта не может повторяться"));
        final IncorrectDataException exception = Assertions.assertThrows(IncorrectDataException.class,
                () -> userService.updateUser(1, userDto));
        Assertions.assertEquals("Почта не может повторяться", exception.getMessage());
    }
}