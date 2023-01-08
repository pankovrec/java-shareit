package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
                                                          Booking nextBooking, Set<CommentDto> comments) {
        return new ItemDtoWithBooking(item.getId(),
                item.getName(), item.getDescription(), item.getAvailable(),
                lastBooking != null ? BookingMapper.toBookingDtoItem(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDtoItem(nextBooking) : null, comments);
    }

    public static List<ItemDto> listToItemDto(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}