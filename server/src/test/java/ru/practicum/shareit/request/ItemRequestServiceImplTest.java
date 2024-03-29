package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService requestService;
    private ItemService itemService;
    private BookingService bookingService;
    private UserService userService;
    private ItemRequestRepository requestRepository = Mockito.mock(ItemRequestRepository.class);
    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final User user1 = new User(1L, "user1", "user1@email.com");
    private final ItemRequest itemRequest1 = new ItemRequest(1L, "test request 1", user1,
            LocalDateTime.now().minusHours(1L));
    private final User secondUser = new User(2, "user2", "user2@mail.ru");
    private final User user = new User(1, "user1", "user1@mail.ru");
    private final Item item = new Item(1, "item1", "item1_desc", true, user, null);
    private final LocalDateTime date = LocalDateTime.of(2022, 8, 20, 13, 12);
    private final Booking booking = new Booking(1, date, date.plusDays(1), item, user, Status.WAITING);
    private final OutBookingDto.Item bookingItem = new OutBookingDto.Item(1, "name");
    private final OutBookingDto.User bookingUser = new OutBookingDto.User(1);
    private final OutBookingDto returnedBookingDto = new OutBookingDto(1, date, date.plusDays(1), bookingItem, bookingUser,
            Status.WAITING);
    private final BookingDto resultingBookingDto = new BookingDto(1, date, date.plusDays(1));
    private final List<Item> items = List.of(
            new Item(1, "item1", "item1_desc", true, user,
                    null),
            new Item(2, "item2", "item2_desc", true, user,
                    null));
    private final List<Item> items2 = List.of(
            new Item(1, "item1", "item1_desc", true, user,
                    null),
            new Item(2, "item2", "item2_desc", true, user,
                    null));
    private final List<ItemRequest> itemRequestList = List.of(
            new ItemRequest(1, "I wanna item1", user, LocalDateTime.now()),
            new ItemRequest(2, "I wanna item2", user, LocalDateTime.now()));
    private final ItemRequest itemRequest = new ItemRequest(1, "I wanna use item1",
            userRepository.getReferenceById(user.getId()), date);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "I wanna use item1",
            user, LocalDateTime.now(), null);
    private final InputRequestItemDto itemRequestDto2 = new InputRequestItemDto("I wanna use item1");
    private final PageImpl<ItemRequest> itemRequestPage = new PageImpl<>(List.of(
            new ItemRequest(1, "I wanna use item1", user, LocalDateTime.now()),
            new ItemRequest(2, "I wanna item2", user, LocalDateTime.now())));
    private final PageImpl<ItemRequest> itemRequestPage2 = new PageImpl<>(List.of());

    @BeforeEach
    void init() {
        requestService = new ItemRequestServiceImpl(userRepository, requestRepository, itemRepository);
        userService = new UserServiceImpl(userRepository);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                requestRepository);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void create() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(secondUser));
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(itemRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(item);
        Mockito
                .when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);
        Mockito
                .when(requestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(itemRequest);
        ItemRequestDto foundedItemRequest = requestService.createRequest(2, itemRequestDto2);

        OutBookingDto foundedReturnedBookingDto = bookingService.createBooking(2, resultingBookingDto);
        assertThat(foundedItemRequest.getId(), equalTo(itemRequestDto.getId()));
        assertThat(foundedReturnedBookingDto.getId(), equalTo(returnedBookingDto.getId()));
        assertThat(foundedReturnedBookingDto.getStart(), equalTo(returnedBookingDto.getStart()));
        assertThat(foundedReturnedBookingDto.getEnd(), equalTo(returnedBookingDto.getEnd()));
        assertThat(foundedReturnedBookingDto.getStatus(), equalTo(returnedBookingDto.getStatus()));
    }

    @Test
    void createFailUserNotExist() {
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Пользователь с id = 1 не найден!"));
        final NotFoundUserException exception = Assertions.assertThrows(NotFoundUserException.class,
                () -> requestService.createRequest(1, itemRequestDto2));
        Assertions.assertEquals("Пользователь с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void createFailUserNotFound() {
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Пользователь с данным id не найден"));
        final NotFoundUserException exception = Assertions.assertThrows(NotFoundUserException.class,
                () -> requestService.createRequest(1, itemRequestDto2));
        Assertions.assertEquals("Пользователь с данным id не найден", exception.getMessage());
    }

    @Test
    void getAllUsersRequests() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(requestRepository.findAllByRequesterId(Mockito.anyLong()))
                .thenReturn(itemRequestList);
        Mockito
                .when(itemRepository.findItemsByRequest(Mockito.anyLong()))
                .thenReturn(items);
        Collection<ItemRequestDto> itemRequestDtoList = requestService.getRequestsByOwner(1);
        for (ItemRequestDto foundedItemRequestDto : itemRequestDtoList) {
            if (foundedItemRequestDto.getId() == (1)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(1L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna item1"));
            } else if (foundedItemRequestDto.getId() == (2)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(2L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna item2"));
            }
        }
    }

    @Test
    void getAllRequests() {
        Mockito
                .when(requestRepository.findAllByRequesterIdIsNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestPage);
        Mockito
                .when(itemRepository.findItemsByRequest(Mockito.anyLong()))
                .thenReturn(items);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Collection<ItemRequestDto> itemRequestDtoList = requestService.getAllRequest(1, 0, 5);
        for (ItemRequestDto foundedItemRequestDto : itemRequestDtoList) {
            if (foundedItemRequestDto.getId() == (1)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(1L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna use item1"));
            } else if (foundedItemRequestDto.getId() == (2)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(2L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna item2"));
            }
        }
    }

    @Test
    public void getAllFail() {
        User user = new User(1L, "user1", "user1@email.com");
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        ArithmeticException thrown = Assertions.assertThrows(ArithmeticException.class, () ->
                requestService.getAllRequest(1, 5, 0));
        Assertions.assertEquals("Неправильно заданы параметры", thrown.getMessage());
    }

    @Test
    void getAllRequestsIfNull() {
        Mockito
                .when(requestRepository.findAllByRequesterIdIsNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(itemRequestPage2);
        Mockito
                .when(itemRepository.findItemsByRequest(Mockito.anyLong()))
                .thenReturn(items2);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Collection<ItemRequestDto> itemRequestDtoList = requestService.getAllRequest(1, 0, 5);
        for (ItemRequestDto foundedItemRequestDto : itemRequestDtoList) {
            if (foundedItemRequestDto.getId() == (1)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(1L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna use item1"));
            } else if (foundedItemRequestDto.getId() == (2)) {
                assertThat(foundedItemRequestDto.getId(), equalTo(2L));
                assertThat(foundedItemRequestDto.getDescription(), equalTo("I wanna item2"));
            }
        }
    }
}