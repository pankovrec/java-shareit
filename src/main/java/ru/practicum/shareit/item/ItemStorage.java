package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    /**
     * добавление вещи
     */
    ItemDto addItem(long userId, Item item);

    /**
     * обновление вещи
     */
    ItemDto updateItem(long userId, ItemDto itemDto, long itemId);

    /**
     * получить вещь по id
     */
    Item getItem(long itemId);

    /**
     * получить список вещей
     */
    Collection<ItemDto> getAllItem(long userId);

    /**
     * поиск вещей
     */
    Collection<ItemDto> searchItems(String text);
}