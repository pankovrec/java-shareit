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
                    itemRequest));

    @Test
    void testConstructor() {
        Set<ItemRequestDto.ItemDto> itemRequestItemDto = ItemRequestMapper.toItemDtoForItemRequestDto(items);
        assertEquals(itemRequestItemDto.size(), 1);
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();
        for (ItemRequestDto.ItemDto s : itemRequestItemDto) {
            itemDto.setId(s.getId());
            itemDto.setRequestId(s.getRequestId());
            itemDto.setAvailable(s.getAvailable());
            itemDto.setDescription(s.getDescription());
            itemDto.setName(s.getName());
        }
        assertEquals(itemDto.getId(), items.get(0).getId());
        assertEquals(itemDto.getAvailable(), items.get(0).getAvailable());
        assertEquals(itemDto.getDescription(), items.get(0).getDescription());
        assertEquals(itemDto.getName(), items.get(0).getName());
        assertEquals(itemDto.getRequestId(), items.get(0).getRequest().getId());
    }
}