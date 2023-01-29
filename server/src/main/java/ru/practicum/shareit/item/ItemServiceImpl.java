package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotAvailableBookingException;
import ru.practicum.shareit.exceptions.NotFoundItemException;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Item service Impl.
 */

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           BookingRepository bookingRepository, CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemDto createItem(ItemDto item, long userId) {
        userCheck(userId);
        checkItemRequestExistsById(item.getRequestId());
        Item item1 = getItem(item, userId);
        Item created = itemRepository.save(item1);
        if (item.getRequestId() == null) {
            return ItemRequestMapper.toItemDtoWithoutItemRequestId(created);
        } else {
            return ItemMapper.toItemDto(itemRepository.save(created));
        }
    }

    private Item getItem(ItemDto itemDto, Long ownerId) {
        User owner = userRepository.getReferenceById(ownerId);
        if (itemDto.getRequestId() == null) {
            return ItemMapper.toItemWithoutItemRequest(itemDto, owner);
        } else {
            ItemRequest itemRequest = itemRequestRepository.getReferenceById(itemDto.getRequestId());
            return ItemMapper.toItem(itemDto, owner, itemRequest);
        }
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto item, long itemId) {
        Item updateItem = itemRepository.findById(itemId).orElseThrow();
        if (updateItem.getOwner().equals(userRepository.findById(userId).orElseThrow())) {
            updateFields(item, updateItem);
            return ItemMapper.toItemDto(itemRepository.save(updateItem));
        } else {
            throw new NotFoundItemException(String.format("У пользователя с id = %s нет вещи с id = %s",
                    userId, itemId));
        }
    }

    @Override
    public ItemDtoWithBooking getItem(long itemId, long userId) {
        userCheck(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(String.format("Вещь с id = %s не найдена", itemId)));
        return getItemResponseDto(item, userId);
    }

    private ItemDtoWithBooking getItemResponseDto(Item item, long userId) {
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking;
        Booking nextBooking;
        bookingList = bookingRepository.findAllByItemsId(item.getId());
        Set<Comment> comments = commentRepository.findCommentsByItem_Id(item.getId());
        if (bookingList.isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = bookingRepository.findLastItemBooking(item.getId(), userId, now);
            nextBooking = bookingRepository.findNextItemBooking(item.getId(), userId, now);
        }
        return ItemMapper.toItemDtoWithBooking(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ItemDtoWithBooking> getAllItem(long userId, int from, int size) {
        int page = paginationParameterCheck(from, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable).getContent();
        List<ItemDtoWithBooking> itemDtoResponseList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDtoWithBooking itemResponseDto = getItemResponseDto(item, userId);
            itemDtoResponseList.add(itemResponseDto);
        }
        return itemDtoResponseList;
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        int page = paginationParameterCheck(from, size);
        Pageable pageable = PageRequest.of(page, size);
        return itemRepository.searchItems(text, pageable).getContent().stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(long userId, long itemId, CommentDtoFromRequest comment) {
        List<Booking> booking = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now());

        if (booking.stream().anyMatch(s -> s.getItem().getId() == itemId)) {
            Comment newComment = new Comment();
            newComment.setItem(itemRepository.findById(itemId).get());
            newComment.setAuthor(userRepository.findById(userId).get());
            newComment.setText(comment.getText());
            return CommentMapper.toCommentDto(commentRepository.save(newComment));
        } else {
            throw new NotAvailableBookingException(String.format(
                    "Пользователь id = %s не брал в аренду вещь id = %s, или аренда еще не завершена",
                    userId, itemId));
        }
    }

    private void updateFields(ItemDto item, Item updateItem) {
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
    }

    private User userCheck(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователь с id = %d не найден!", userId)));
    }

    private void checkItemRequestExistsById(Long requestId) {
        if (requestId != null && !itemRequestRepository.existsById(requestId)) {
            throw new NotFoundItemException("Нет такого запроса");
        }
    }

    private Integer paginationParameterCheck(int from, int size) {
        if (size > 0 && from >= 0) {
            int page = from / size;
            return page;
        } else {
            throw new ArithmeticException("Неверные параметры поиска.");
        }
    }
}