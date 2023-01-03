package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserServiceImpl.checkUserExistsById;

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
        checkUserExistsById(userRepository, userId);
        checkUserNotOwnerByItemIdAndUserId(bookingDto.getItemId(), userId);
        checkBookingTimePeriod(bookingDto.getStart(), bookingDto.getEnd());

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundItemException(
                        String.format("Вещь id = %s не найдена", bookingDto.getItemId())));

        if (item.getOwner().getId() == userId) {
            throw new NotFoundUserException((String.format(
                    "Невозможно бронирование собственное вещи пользователем %s",
                    userId)));
        }
        if (!item.getAvailable()) {
            throw new NotAvailableBookingException(String.format(
                    "Вещь с id = %s недоступна для бронирования",
                    bookingDto.getItemId()));
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        booking.setBooker(userRepository.findById(userId).orElseThrow());
        log.info("Пользователь id = {} бронирует вещь id = {}", userId, bookingDto.getItemId());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public OutBookingDto getBooking(long bookingId, long userId) {
        checkUserExistsById(userRepository, userId);
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
        checkUserExistsById(userRepository, userId);
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
    public List<OutBookingDto> getAllBookingByUser(long userId, State state) {
        checkUserExistsById(userRepository, userId);

        switch (state) {
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findCurrentBooking(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED)
                        .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<OutBookingDto> getAllBookingByOwner(long userId, State state) {
        checkUserExistsById(userRepository, userId);

        switch (state) {
            case PAST:
                return bookingRepository.findBookingByOwnerPast(userId,
                        LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findBookingByOwnerFuture(userId,
                        LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findBookingByOwnerCurrent(userId,
                        LocalDateTime.now()).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllStatusByItemsOwnerId(userId, Status.WAITING).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllStatusByItemsOwnerId(userId, Status.REJECTED).stream()
                        .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case ALL:
                return bookingRepository.findBookingByOwner(userId).stream().map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findBookingByOwnerAndStatus(
                        userId, state).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    private void checkBookingTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new IncorrectDataException(String.format("Дата окончания %s должна быть после даты начала %s",
                    end, start));
        }
    }

    private void checkUserNotOwnerByItemIdAndUserId(long itemId, long userId) {
        Long ownerId = itemRepository.getReferenceById(itemId).getOwner().getId();
        if (ownerId.equals(userId)) {
            throw new UserNotOwnerException(String.format("Пользователь с id %d не может забронировать свою вещь %d",
                    ownerId, itemId));
        }
    }

    private void checkBookingStatusNotApprove(Booking booking) {
        if (booking.getStatus() == Status.APPROVED) {
            throw new NotAvailableBookingException(String.format("Бронирование уже подтверждено %s", booking.getId()));
        }
    }
}