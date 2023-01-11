package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Item mapper.
 */

public class ItemMapper {
    public static Item toItem(ItemDto itemDto, User user, ItemRequest request) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), user, request);
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null);
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, Booking lastBooking,
                                                          Booking nextBooking, Set<Comment> comments) {
        return new ItemDtoWithBooking(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(),
                lastBooking != null ? BookingMapper.toBookingDtoItem(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDtoItem(nextBooking) : null, toCommentDto(comments));
    }

    public static ItemDtoWithBooking.CommentDto toCommentDto(Comment comment) {
        ItemDtoWithBooking.CommentDto commentDto = new ItemDtoWithBooking.CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Set<ItemDtoWithBooking.CommentDto> toCommentDto(Set<Comment> comments) {
        return comments.stream().map(ItemMapper::toCommentDto).collect(Collectors.toSet());
    }

    public static List<ItemDto> listToItemDto(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}