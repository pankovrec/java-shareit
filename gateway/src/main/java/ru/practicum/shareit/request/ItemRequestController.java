package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid ItemRequestDto request) {
        return itemRequestClient.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.getRequestsByOwner(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PositiveOrZero @RequestParam
                                                         (required = false, defaultValue = "0") int from,
                                                 @Positive @RequestParam
                                                         (required = false, defaultValue = "10") int size) {
        return itemRequestClient.getAllRequest(userId, from, size);
    }

    @GetMapping(path = "/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
