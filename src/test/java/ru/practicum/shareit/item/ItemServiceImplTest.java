package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class ItemServiceImplTest {

    ItemService itemService;
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);

    ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    @InjectMocks
    private BookingServiceImpl service;

    private final Item item1 = new Item(1L, "item1", "item1_desc", true, null,
            null);
    private final User user1 = new User(1L, "user1", "user1@mail.ru");
    private final User user2 = new User(2L, "user2", "user2@mail.ru");
    private final Booking booking1 = new Booking(1L, LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(1), item1, user1, null);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now());
    // User user = new User(1, "user1", "user1@mail.ru");
    User user = new User(1, "user1", "user1@mail.ru");
    Item item = new Item(1, "item1", "item1_desc", true, user, null);
    List<User> users = List.of(new User(1, "user1", "user1@mail.ru"),
            new User(2, "user2", "user2@mail.ru"),
            new User(3, "user3", "user3@mail.ru"));
    LocalDateTime date = LocalDateTime.of(2022, 8, 20, 13, 12);
    Booking booking = new Booking(1, date, date.plusDays(1), item, user, Status.WAITING);
    Booking secondBooking = new Booking(2, date.plusDays(2), date.plusDays(3),
            item, user, Status.WAITING);
    ItemDto itemDto = new ItemDto(1, "item1", "item1_desc", true,
            null);

    PageImpl<Item> itemPage = new PageImpl<>(List.of(
            new Item(1, "item1", "item1_desc", true, user, null)));

    @BeforeEach
    void init() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }

    @Test
    void create() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        ItemDto foundedItemDto = itemService.createItem(itemDto, 1L);
        assertThat(foundedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(foundedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(foundedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void createUserNotExist() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Пользователь с id = 1 не найден!"));
        final NotFoundUserException exception = Assertions.assertThrows(
                NotFoundUserException.class, () -> itemService.createItem(itemDto, 1L));
        Assertions.assertEquals("Пользователь с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void createUserNotFound() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(users);
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenThrow(new NotFoundUserException("Пользователь с данным id не найден в базе"));
        final NotFoundUserException exception = Assertions.assertThrows(
                NotFoundUserException.class, () -> itemService.createItem(itemDto, 1L));
        Assertions.assertEquals("Пользователь с данным id не найден в базе", exception.getMessage());
    }


    @Test
    void getById() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.findLastItemBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(booking);
        Mockito
                .when((bookingRepository.findNextItemBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class))))
                .thenReturn(secondBooking);

        ItemDtoWithBooking foundedItemDto = itemService.getItem(1, 1L);
        assertThat(foundedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(foundedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(foundedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));

    }

    @Test
    void getAll() {
        Mockito
                .when(userRepository.getReferenceById(Mockito.anyLong()))
                .thenReturn(user);
        Mockito
                .when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(itemPage);
        Mockito
                .when(bookingRepository.findLastItemBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class)))
                .thenReturn(booking);
        Mockito
                .when((bookingRepository.findNextItemBooking(Mockito.anyLong(), Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class))))
                .thenReturn(secondBooking);
        Collection<ItemDtoWithBooking> itemDtos = itemService.getAllItem(1L, 0, 5);
        for (ItemDto foundedItemDto : itemDtos) {
            if (foundedItemDto.getId() == (1)) {
                assertThat(foundedItemDto.getId(), equalTo(itemDto.getId()));
                assertThat(foundedItemDto.getName(), equalTo(itemDto.getName()));
                assertThat(foundedItemDto.getDescription(), equalTo(itemDto.getDescription()));
                assertThat(foundedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
            }
        }
    }

    @Test
    void update() {
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(user));
        item.setOwner(user);
        Mockito
                .when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        ItemDto foundedItemDto = itemService.updateItem(1, itemDto, 1);
        assertThat(foundedItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(foundedItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(foundedItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(foundedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void search() {
        Mockito
                .when(itemRepository.searchItems(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(itemPage);
        Collection<ItemDto> itemDtos = itemService.searchItems("item1", 0, 5);
        for (ItemDto foundedItemDto : itemDtos) {
            if (foundedItemDto.getId() == (1)) {
                assertThat(foundedItemDto.getId(), equalTo(itemDto.getId()));
                assertThat(foundedItemDto.getName(), equalTo(itemDto.getName()));
                assertThat(foundedItemDto.getDescription(), equalTo(itemDto.getDescription()));
                assertThat(foundedItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
            }
        }
    }
}