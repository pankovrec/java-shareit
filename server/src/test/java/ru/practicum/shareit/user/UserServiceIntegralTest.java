package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegralTest {

    private final UserService userService;
    private final UserDto userDto = new UserDto(1, "user1", "user1@mail.ru");

    @Test
    void getById() {
        userService.createUser(userDto);
        UserDto foundedUserDto = userService.getUser(1);
        assertThat(foundedUserDto.getId(), equalTo(1L));
        assertThat(foundedUserDto.getName(), equalTo("user1"));
        assertThat(foundedUserDto.getEmail(), equalTo("user1@mail.ru"));
    }

    @Test
    void getByIdFail() {
        RuntimeException runtimeException = assertThrows(NotFoundUserException.class, () ->
                userService.getUser(16654)
        );
        String message = "Пользователь с id = " + 16654 + " не найден!";
        String actual = runtimeException.getMessage();
        assertEquals(actual, message);
    }
}
