package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * ItemRequestService.
 */
public interface ItemRequestService {

    /**
     * создать запрос вещи
     */
    ItemRequestDto createRequest(long userId, InputRequestItemDto request);

    /**
     * получить все запросы пользователя
     */
    List<ItemRequestDto> getRequestsByOwner(long userId);

    /**
     * получить все запросы вещей(pageable)
     */
    List<ItemRequestDto> getAllRequest(long userId, int from, int size);

    /**
     * получить запросы вещи по id
     */
    ItemRequestDto getRequestById(long userId, long requestId);
}