package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    /**
     * добавление вещи
     */
    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(long userId, ItemDto item, long itemId);

    ItemDtoWithBooking getItem(long itemId, long userId);

    /**
     * получить все вещи
     */
    Collection<ItemDtoWithBooking> getAllItem(long userId);

    /**
     * поиск вещи
     */
    List<ItemDto> searchItems(String text);

    /**
     * оставить комментарий
     */
    CommentDto createComment(long userId, long itemId, CommentDtoFromRequest comment);

}
