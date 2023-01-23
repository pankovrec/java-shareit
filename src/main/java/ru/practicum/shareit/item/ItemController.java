package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoFromRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * ItemController
 * //
 */

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoWithBooking> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllItem(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @Valid @RequestBody CommentDtoFromRequest comment,
                                    @PathVariable long itemId) {
        return itemService.createComment(userId, itemId, comment);
    }
}