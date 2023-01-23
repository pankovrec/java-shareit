package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundItemException;
import ru.practicum.shareit.exceptions.NotFoundUserException;
import ru.practicum.shareit.item.ItemRepository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.InputRequestItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ItemRequestServiceImpl.
 */

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(UserRepository userRepository,
                                  ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;

    }

    @Override
    public ItemRequestDto createRequest(long userId, InputRequestItemDto request) {
        User user = userCheck(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(request, user, LocalDateTime.now());
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.toItemRequestDtoWithoutItems(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwner(long userId) {
        userCheck(userId);
        log.info("Запрошены все запросы пользователя id = {}", userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId);
        return getListOfItemRequestsDto(itemRequests);
    }

    @Override
    public List<ItemRequestDto> getAllRequest(long userId, int from, int size) {
        userCheck(userId);
        if (size > 0 && from >= 0) {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size, Sort.by("created").descending());
            Page<ItemRequest> itemRequestPage = itemRequestRepository.findAllByRequesterIdIsNotLike(userId, pageable);
            log.info("Пользователь id = {} запросил все запросы", userId);

            return getListOfItemRequestsDto(itemRequestPage.toList());
        } else {
            throw new ArithmeticException(
                    "Неправильно заданы параметры");
        }
    }

    @Override
    public ItemRequestDto getRequestById(long userId, long requestId) {
        userCheck(userId);
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundItemException("Нет такого запроса");
        }
        ItemRequest itemRequest = itemRequestRepository.getReferenceById(requestId);
        List<Item> items = itemRepository.findItemsByRequest(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, items);
        log.info("Пользователь id = {} запросил запросы id = {}", userId, requestId);
        return itemRequestDto;
    }

    private User userCheck(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователь с id = %d не найден!", userId)));
        return userRepository.getReferenceById(userId);
    }

    private List<ItemRequestDto> getListOfItemRequestsDto(List<ItemRequest> itemRequests) {
        Map<Long, List<Item>> itemsByItemRequestId = new HashMap<>();
        List<Item> allItems = itemRepository.findAll();

        for (Item item : allItems) {
            if (item.getRequest() == null) {
                continue;
            }
            Long currentItemRequestId = item.getRequest().getId();

            List<Item> itemsByCurrentItemRequestId =
                    itemsByItemRequestId.getOrDefault(currentItemRequestId, new ArrayList<>());
            itemsByCurrentItemRequestId.add(item);

            itemsByItemRequestId.put(currentItemRequestId, itemsByCurrentItemRequestId);
        }
        return itemRequests.stream()
                .map(itemRequest -> getItemRequestDto(itemRequest, itemsByItemRequestId))
                .collect(Collectors.toList());
    }

    private static ItemRequestDto getItemRequestDto(ItemRequest itemRequest,
                                                    Map<Long, List<Item>> itemsByItemRequestId) {
        List<Item> items = itemsByItemRequestId.getOrDefault(itemRequest.getId(), new ArrayList<>());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }
}
