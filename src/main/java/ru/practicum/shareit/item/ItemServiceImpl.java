package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) {
        if (userStorage.getUser(userId) != null) {
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(UserMapper.toUser(userStorage.getUser(userId)));
            return itemStorage.addItem(userId, item);
        } else {
            return null;
        }
    }

    @Override
    public ItemDto updateItem(long userId, ItemDto itemDto, long itemId) {
        if (userStorage.getUser(userId) != null) {
            return itemStorage.updateItem(userId, itemDto, itemId);
        } else {
            return null;
        }
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItem(long userId) {
        return itemStorage.getAllItem(userId);
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (!text.isEmpty()) {
            return itemStorage.searchItems(text);
        } else {
            return new ArrayList<>();
        }
    }
}