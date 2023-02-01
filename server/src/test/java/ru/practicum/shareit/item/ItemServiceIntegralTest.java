package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.exceptions.NotAvailableBookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShareItApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegralTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;
    private final UserDto user1 = new UserDto(1L, "User1", "user1@test.ru");
    private final UserDto user2 = new UserDto(2L, "User2", "user2@test.ru");
    private final InputRequestItemDto request = new InputRequestItemDto();
    private final ItemDto itemDto = new ItemDto();
    private final CommentDtoFromRequest commentDto = new CommentDtoFromRequest();
    private final BookingDto lastBookingDto = new BookingDto(
            1L,
            LocalDateTime.now().minusDays(5),
            LocalDateTime.now().minusDays(2)
    );

    @BeforeEach
    public void init() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();
        em.createNativeQuery("TRUNCATE table items restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table users restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table booking restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table requests restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table comments restart identity;").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();
        user1.setName("User1");
        user1.setEmail("user1@test.ru");
        user2.setName("User2");
        user2.setEmail("user2@test.ru");
        userService.createUser(user1);
        userService.createUser(user2);
        request.setDescription("Request item");
        itemRequestService.createRequest(user2.getId(), request);
        itemDto.setRequestId(1L);

        itemDto.setAvailable(true);
        itemDto.setDescription("Description item");
        itemDto.setName("New item");
    }

    @Test
    public void createItem() {
        ItemDto item = itemService.createItem(itemDto, user1.getId());
        assertThat(item.getId(), equalTo(1L));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getAvailable(), equalTo(true));
    }

    @Test
    public void updateItem() {
        ItemDto item = itemService.createItem(itemDto, user1.getId());
        ItemDto updateItem = new ItemDto();
        updateItem.setName("Update name");
        updateItem.setDescription("Description after update");
        updateItem.setAvailable(false);
        updateItem.setRequestId(1L);
        ItemDto itemAfterUpdate = itemService.updateItem(user1.getId(), updateItem, item.getId());
        assertThat(itemAfterUpdate.getId(), equalTo(1L));
        assertThat(itemAfterUpdate.getDescription(), equalTo(updateItem.getDescription()));
        assertThat(itemAfterUpdate.getName(), equalTo(updateItem.getName()));
        assertThat(itemAfterUpdate.getAvailable(), equalTo(false));
    }

    @Test
    public void getItem() {
        ItemDto item = itemService.createItem(itemDto, user1.getId());
        commentDto.setText("Comment for item1");
        ItemDtoWithBooking itemDtoWithBooking = itemService.getItem(item.getId(), user1.getId());
        assertThat(itemDtoWithBooking.getId(), equalTo(1L));
        assertThat(itemDtoWithBooking.getName(), equalTo(item.getName()));
        assertThat(itemDtoWithBooking.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDtoWithBooking.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    public void createComment() {
        ItemDto item1 = itemService.createItem(itemDto, user1.getId());
        OutBookingDto lastBooking = bookingService.createBooking(user2.getId(), lastBookingDto);
        commentDto.setText("Comment for item1");
        CommentDto comment = itemService.createComment(user2.getId(), item1.getId(), commentDto);
        assertThat(comment.getId(), equalTo(1L));
        assertThat(comment.getCreated(), notNullValue());
        assertThat(comment.getAuthorName(), equalTo(user2.getName()));
        assertThat(comment.getText(), equalTo(commentDto.getText()));
    }

    @Test
    public void createCommentFail() {
        ItemDto item1 = itemService.createItem(itemDto, user1.getId());
        OutBookingDto lastBooking = bookingService.createBooking(user2.getId(), lastBookingDto);
        commentDto.setText("Comment for item1");
        NotAvailableBookingException thrown = Assertions.assertThrows(NotAvailableBookingException.class, () ->
                itemService.createComment(user1.getId(), item1.getId(), commentDto));
        Assertions.assertEquals("Пользователь id = 1 не брал в аренду вещь id = 1, или аренда еще не завершена",
                thrown.getMessage());
    }
}