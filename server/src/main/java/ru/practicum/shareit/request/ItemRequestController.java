package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

/**
 * ItemRequestController.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @RequestBody InputRequestItemDto request) {
        return itemRequestService.createRequest(userId, request);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getRequestsByOwner(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        return itemRequestService.getAllRequest(userId, from, size);
    }

    @GetMapping(path = "/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}
