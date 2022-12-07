package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(long userId, ItemDto itemDto);

    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    ItemDto getItem(long itemId);

    Collection<ItemDto> getAllItem(long userId);

    Collection<ItemDto> searchItems(String text);
}
