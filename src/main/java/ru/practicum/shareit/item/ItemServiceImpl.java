package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserServiceImpl.checkUserExistsById;

/**
 * Item service Impl.
 */

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto item, long userId) {
        checkUserExistsById(userRepository, userId);
        Item created = ItemMapper.toItem(item);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundUserException("Пользователь с id " + userId + " не найден"));
        created.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(created));
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
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(String.format("Вещь с id = %s не найдена", itemId)));
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.toItemDtoWithBooking(item);
        if (item.getOwner().getId() == userId) {
            setBookings(itemDtoWithBooking, itemId);
        }
        Set<Comment> comments = commentRepository.findCommentsByItem_Id(itemId);
        if (!comments.isEmpty()) {
            itemDtoWithBooking.setComments(
                    comments.stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toSet()));
        }
        return itemDtoWithBooking;
    }

    @Override
    public Collection<ItemDtoWithBooking> getAllItem(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        items.sort(Comparator.comparing(Item::getId));
        List<ItemDtoWithBooking> itemsDto = items.stream().
                map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toList());
        for (int i = 0; i < itemsDto.size(); i++) {
            setBookings(itemsDto.get(i), items.get(i).getId());
            Set<Comment> comments = commentRepository.findCommentsByItem_Id(itemsDto.get(i).getId());
            if (!comments.isEmpty()) {
                itemsDto.get(i).setComments(
                        comments.stream()
                                .map(CommentMapper::toCommentDto)
                                .collect(Collectors.toSet()));
            }
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> items = new ArrayList<>();
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        List<Item> founds = itemRepository.searchItems(text);
        for (Item item : founds) {
            if (item.getAvailable()) {
                items.add(item);
            }
        }
        return ItemMapper.listToItemDto(items);
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

    private void setBookings(ItemDtoWithBooking itemDtoWithBooking, long itemId) {
        Booking lastBooking = bookingRepository.findLastItemBooking(itemId, LocalDateTime.now());
        if (lastBooking != null) {
            itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDtoItem(lastBooking));
        }
        Booking nextBooking = bookingRepository.findNextItemBooking(itemId, LocalDateTime.now());
        if (nextBooking != null) {
            itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDtoItem(nextBooking));
        }
    }
}