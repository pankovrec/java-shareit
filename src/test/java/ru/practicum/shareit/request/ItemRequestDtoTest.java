package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestDtoTest {
    User user = new User(1L, "user", "user1@mail.ru");
    ItemRequest itemRequest = new ItemRequest(1, "I wanna use item1",
            user, LocalDateTime.of(1, 1, 1, 1, 1));
    List<Item> items = List.of(
            new Item(1, "item1", "item1_desc", true, user,
                    itemRequest),
            new Item(2, "item2", "item2_desc", true, user,
                    itemRequest));

    @Test
    void testConstructor() {
        Set<ItemRequestDto.ItemDto> itemRequestItemDto = ItemRequestMapper.toItemDtoForItemRequestDto(items);
        assertEquals(itemRequestItemDto.size(), 2);
    }
}
