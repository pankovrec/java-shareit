package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.exceptions.NotFoundUserException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id;

    @Override
    public ItemDto addItem(long userId, Item item) {
        item.setId(makeId());
        if (items.containsKey(userId)) {
            items.get(userId).add(item);
        } else {
            items.put(userId, new ArrayList<>());
            items.get(userId).add(item);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (items.get(userId) != null) {
            if (items.get(userId).contains(getItem(itemId))) {
                Item newItem = getItem(itemId);
                if (itemDto.getId() != 0L) {
                    newItem.setId(itemDto.getId());
                }
                if (itemDto.getName() != null) {
                    newItem.setName(itemDto.getName());
                }
                if (itemDto.getDescription() != null) {
                    newItem.setDescription(itemDto.getDescription());
                }
                if (itemDto.getAvailable() != null) {
                    newItem.setAvailable(itemDto.getAvailable());
                }
                deleteItem(userId, itemId);
                items.get(userId).add(newItem);
                return ItemMapper.toItemDto(newItem);
            } else {
                throw new NotFoundItemException(String.format("У пользователя %s нет вещи с id = %s",
                        userId, itemId));
            }
        } else {
            throw new NotFoundUserException(String.format("Нет пользователя с id = %s", userId));
        }
    }

    @Override
    public Item getItem(long itemId) {
        Item item = null;
        while (item == null) {
            for (List<Item> itemList : items.values()) {
                item = itemList.stream()
                        .filter(i -> i.getId() == itemId)
                        .findFirst()
                        .orElse(null);
            }
        }
        return item;
    }

    @Override
    public Collection<ItemDto> getAllItem(long userId) {
        return items.get(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        List<ItemDto> findItems = new ArrayList<>();
        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        && item.getAvailable()) {
                    findItems.add(ItemMapper.toItemDto(item));
                }
            }
        }
        return findItems;
    }

    private void deleteItem(long userId, long itemId) {
        items.get(userId).removeIf(item -> item.getId() == itemId);
    }

    private long makeId() {
        return (id == 0L) ? id = 1L : ++id;
    }
}