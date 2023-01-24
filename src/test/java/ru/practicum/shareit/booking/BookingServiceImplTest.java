package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.State.ALL;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl service;
    private final Item item1 = new Item(1L, "item1", "item1_desc", true, null,
            null);
    private final User user1 = new User(1L, "user1", "user1@mail.ru");
    private final User user2 = new User(2L, "user2", "user2@mail.ru");
    private final Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1), item1, user1, null);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now());
    User user = new User(1, "user1", "user1@mail.ru");

    @Test
    public void createBooking() {
        item1.setOwner(user2);
        bookingDto.setItemId(1L);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user2));
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking1);
        OutBookingDto booking = service.createBooking(user1.getId(), bookingDto);
        Assertions.assertEquals(BookingMapper.toBookingDto(booking1).toString(), booking.toString());
    }

    @Test
    public void createBookingItemNotFound() {
        bookingDto.setItemId(1L);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        NotFoundItemException thrown = Assertions.assertThrows(NotFoundItemException.class, () ->
                service.createBooking(1L, bookingDto));
        Assertions.assertEquals("Вещь с id = 1 не найдена!", thrown.getMessage());
    }

    @Test
    public void createItemNotAvailable() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        booking1.setBooker(user2);
        bookingDto.setItemId(1L);
        item1.setOwner(user2);
        item1.setAvailable(false);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item1));
        NotAvailableBookingException thrown = Assertions.assertThrows(NotAvailableBookingException.class, () ->
                service.createBooking(1L, bookingDto));
        Assertions.assertEquals("Вещь с id = 1 недоступна для бронирования", thrown.getMessage());
    }

    @Test
    public void getBookingOwnerIsUser() {
        booking1.setId(1L);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        booking1.setBooker(user2);
        item1.setOwner(user1);
        booking1.setItem(item1);

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        OutBookingDto booking = service.getBooking(booking1.getId(), user1.getId());
        Assertions.assertEquals(BookingMapper.toBookingDto(booking1).toString(), booking.toString());
    }

    @Test
    public void getBookingUserNotOwner() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        booking1.setId(1L);
        booking1.setBooker(user2);
        item1.setOwner(user2);
        booking1.setItem(item1);
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        service.checkItemOwner(user.getId(), item1);
        UserNotOwnerException thrown = assertThrows(UserNotOwnerException.class, () ->
                service.getBooking(booking1.getId(), user1.getId()));
        Assertions.assertEquals("Пользователь 1 не может запрашивать информацию о бронировании 1",
                thrown.getMessage());
    }

    @Test
    public void getBookingIfBookingNotFound() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        booking1.setBooker(user2);
        item1.setOwner(user1);
        booking1.setId(1L);
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        NotFoundBookingException thrown = Assertions.assertThrows(NotFoundBookingException.class, () ->
                service.getBooking(booking1.getId(), user1.getId()));
        Assertions.assertEquals("Бронирование id = 1 не найдено",
                thrown.getMessage());
    }


    @Test
    public void changeStatusIfNotOwner() {
        booking1.setId(1L);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        booking1.setBooker(user2);
        item1.setOwner(user2);
        booking1.setId(1L);
        booking1.setItem(item1);
        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        UserNotOwnerException thrown = Assertions.assertThrows(UserNotOwnerException.class, () ->
                service.updateBookingStatus(booking1.getId(), true, user1.getId()));
        Assertions.assertEquals("Данная вещь не принадлежит юзеру id = 1",
                thrown.getMessage());
    }

    @Test
    public void changeStatusIfStatusIsAproovedNow() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));

        booking1.setBooker(user2);
        item1.setOwner(user1);
        booking1.setId(1L);
        booking1.setItem(item1);
        booking1.setStatus(Status.APPROVED);

        Mockito
                .when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking1));
        NotAvailableBookingException thrown = Assertions.assertThrows(NotAvailableBookingException.class, () ->
                service.updateBookingStatus(booking1.getId(), true, user1.getId()));
        Assertions.assertEquals("Бронирование уже подтверждено 1",
                thrown.getMessage());
    }

}