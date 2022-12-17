package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    /**
     * добавление вещи
     */
    ItemDto createItem(long userId, ItemDto itemDto);

    /**
     * обновление вещи
     */
    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    /**
     * получить вещь по id
     */
    ItemDto getItem(long itemId);

    /**
     * получить все вещи
     */
    Collection<ItemDto> getAllItem(long userId);

    /**
     * поиск вещи
     */
    Collection<ItemDto> searchItems(String text);
}
