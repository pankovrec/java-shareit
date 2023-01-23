package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ItemRequestMapper.
 */

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setItems(toItemDtoForItemRequestDto(items));
        return itemRequestDto;
    }

    private static Set<ItemRequestDto.ItemDto> toItemDtoForItemRequestDto(Collection<Item> items) {
        return items.stream()
                .map(ItemRequestMapper::toItemDtoForItemRequestDto)
                .collect(Collectors.toSet());
    }

    public static ItemRequestDto toItemRequestDtoWithoutItems(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    private static ItemRequestDto.ItemDto toItemDtoForItemRequestDto(Item item) {
        ItemRequestDto.ItemDto itemDto = new ItemRequestDto.ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());

        return itemDto;
    }

    public static ItemDto toItemDtoWithoutItemRequestId(Item item) {
        ItemDto itemDto = new ItemDto();

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());

        return itemDto;
    }

    public static ItemRequest toItemRequest(InputRequestItemDto itemRequestDto,
                                            User requester, LocalDateTime time) {
        ItemRequest itemRequest = new ItemRequest();

        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(time);

        return itemRequest;
    }
}