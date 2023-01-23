package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegralTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserDto user1 = new UserDto(1, "User1", "user1@mail.ru");
    private final UserDto user2 = new UserDto(2, "User2", "user2@mail.ru");
    private ItemDto item1 = new ItemDto();
    private BookingDto bookingDto;

    @BeforeEach
    public void init() {
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE;").executeUpdate();
        em.createNativeQuery("TRUNCATE table items restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table users restart identity;").executeUpdate();
        em.createNativeQuery("TRUNCATE table booking restart identity;").executeUpdate();
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE;").executeUpdate();
        userService.createUser(user1);
        userService.createUser(user2);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item1");
        itemDto.setDescription("item1_desc");
        itemDto.setAvailable(true);
        item1 = itemService.createItem(itemDto, user2.getId());
        bookingDto = new BookingDto(item1.getId(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    public void create() {
        OutBookingDto booking = bookingService.createBooking(user1.getId(), bookingDto);
        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getStart(), notNullValue());
        assertThat(booking.getEnd(), notNullValue());
    }

    @Test
    public void getById() {
        bookingService.createBooking(user1.getId(), bookingDto);
        OutBookingDto booking = bookingService.getBooking(1L, user1.getId());
        OutBookingDto.Item item = new OutBookingDto.Item(1, "name");
        booking.setItem(item);
        OutBookingDto.User booker = new OutBookingDto.User(1);
        booking.setBooker(booker);
        assertThat(booking.getId(), equalTo(1L));
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        assertThat(booking.getStart(), notNullValue());
        assertThat(booking.getEnd(), notNullValue());
    }

    @Test
    public void updateBookingStatus() {
        OutBookingDto booking = bookingService.createBooking(user1.getId(), bookingDto);
        assertThat(booking.getStatus(), equalTo(Status.WAITING));
        booking = bookingService.updateBookingStatus(booking.getId(), false, user2.getId());
        assertThat(booking.getStatus(), equalTo(Status.REJECTED));
        booking = bookingService.updateBookingStatus(booking.getId(), true, user2.getId());
        assertThat(booking.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    public void getAllByUser() {
        OutBookingDto booking = bookingService.createBooking(user1.getId(), bookingDto);
        OutBookingDto.Item item = new OutBookingDto.Item(1, "item1");
        booking.setItem(item);
        OutBookingDto.User booker = new OutBookingDto.User(1);
        booking.setBooker(booker);
        List<OutBookingDto> bookingList = bookingService.getAllBookingByUser(user1.getId(), State.ALL, 0, 5);
        assertThat(bookingList.get(0).toString(), equalTo(booking.toString()));
    }

    @Test
    public void getAllByOwner() {
        OutBookingDto booking = bookingService.createBooking(user1.getId(), bookingDto);
        OutBookingDto.Item item = new OutBookingDto.Item(1, "item1");
        booking.setItem(item);
        OutBookingDto.User booker = new OutBookingDto.User(1);
        booking.setBooker(booker);
        List<OutBookingDto> bookingList = bookingService.getAllBookingByOwner(user2.getId(), State.ALL, 0, 5);
        assertThat(bookingList.get(0).toString(), equalTo(booking.toString()));
    }
}
