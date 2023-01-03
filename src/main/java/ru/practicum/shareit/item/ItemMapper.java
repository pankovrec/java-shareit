package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Item mapper.
 */

public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item) {
        return new ItemDtoWithBooking(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static List<ItemDto> listToItemDto(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}