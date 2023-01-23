package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BookingServiceImpl
 */

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OutBookingDto createBooking(long userId, BookingDto bookingDto) {
        checkBookingDate(bookingDto);
        User booker = userCheck(userId);
        Item item = itemCheck(bookingDto.getItemId());

        checkItemOwner(userId, item);
        if (!item.getAvailable()) {
            throw new NotAvailableBookingException(String.format(
                    "Вещь с id = %s недоступна для бронирования",
                    bookingDto.getItemId()));
        }
        Booking booking = new Booking(0, bookingDto.getStart(), bookingDto.getEnd(),
                item, booker, Status.WAITING);
        log.info("Пользователь id = {} бронирует вещь id = {}", userId, bookingDto.getItemId());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    private User userCheck(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователь с id = %d не найден!", userId)));
    }

    public Item itemCheck(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    @Override
    public OutBookingDto getBooking(long bookingId, long userId) {
        userCheck(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundBookingException(
                        String.format("Бронирование id = %s не найдено", bookingId)));
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            log.info("Пользователь id = {} запрашивает информацию о бронировании id = {}", userId, bookingId);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new NotFoundBookingException(
                    String.format("Пользователь %s не может запрашивать информацию о бронировании %s",
                            userId, bookingId));
        }
    }

    @Override
    public OutBookingDto updateBookingStatus(long bookingId, boolean approved, long userId) {
        userCheck(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundBookingException(
                        String.format("Бронирование id = %s не найдено", bookingId)));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundUserException(String.format("Данная вещь не принадлежит юзеру id = %s", userId));
        }
        checkBookingStatusNotApprove(booking);
        booking.setStatus((approved == Boolean.TRUE) ? (Status.APPROVED) : (Status.REJECTED));
        log.info("Изменен статус бронирования id = {}", bookingId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }


    @Override
    public List<OutBookingDto> getAllBookingByUser(long userId, State state, int from, int size) {
        userCheck(userId);
        if (size > 0 && from >= 0) {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
            switch (state) {
                case PAST:
                    return bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                                    LocalDateTime.now(), pageable).stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                                    LocalDateTime.now(), pageable).stream()
                            .map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findCurrentBooking(userId, LocalDateTime.now(), pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                                    pageable).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                                    pageable).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                default:
                    return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            }

        } else {
            throw new ArithmeticException("Ошибка в индекса первого элемента или количества элементов для отображения");
        }
    }

    @Override
    public List<OutBookingDto> getAllBookingByOwner(long userId, State state, int from, int size) {
        userCheck(userId);
        if (size > 0 && from >= 0) {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, Sort.by("start").descending());
            switch (state) {
                case PAST:
                    return bookingRepository.findBookingByOwnerPast(userId,
                                    LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case FUTURE:
                    return bookingRepository.findBookingByOwnerFuture(userId,
                                    LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case CURRENT:
                    return bookingRepository.findBookingByOwnerCurrent(userId,
                                    LocalDateTime.now(), pageable).stream().map(BookingMapper::toBookingDto)
                            .collect(Collectors.toList());
                case WAITING:
                    return bookingRepository.findAllStatusByItemsOwnerId(userId, Status.WAITING, pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case REJECTED:
                    return bookingRepository.findAllStatusByItemsOwnerId(userId, Status.REJECTED, pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                case ALL:
                    return bookingRepository.findBookingByOwner(userId, pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
                default:
                    return bookingRepository.findBookingByOwnerAndStatus(userId, state, pageable).stream()
                            .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            }
        } else {
            throw new ArithmeticException("Ошибка в индекса первого элемента или количества элементов для отображения");
        }
    }

    private void checkItemOwner(long userId, Item item) {
        if (userId == item.getOwner().getId()) {
            throw new UserNotOwnerException(String.format("Пользователь с id %d не может забронировать свою вещь %d",
                    userId, item.getId()));
        }
    }

    private void checkBookingDate(BookingDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new IncorrectDataException("Дата окончания должна быть после даты начала");
        }
    }

    private void checkBookingStatusNotApprove(Booking booking) {
        if (booking.getStatus() == Status.APPROVED) {
            throw new NotAvailableBookingException(String.format("Бронирование уже подтверждено %s", booking.getId()));
        }
    }
}