package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto requestDto) {
        User requester = throwIfUserNotExist(userId);
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);
        request.setRequester(requester);
        request = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(request);
    }

    @Override
    public Collection<ItemRequestDto> getByUser(Long userId) {
        throwIfUserNotExist(userId);
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> requestsIds = requests.stream().map(ItemRequest::getId).toList();
        Collection<Item> items = itemRepository.findAllByRequestIdIn(requestsIds);

        Map<Long, List<Item>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request,
                        itemsByRequest.getOrDefault(request.getId(), List.of())))
                .toList();
    }

    @Override
    public Collection<ItemRequestDto> getAll(Long userId) {
        throwIfUserNotExist(userId);
        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);
        if (requests.isEmpty()) {
            return Collections.emptyList();
        }
        return requests.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        throwIfUserNotExist(userId);
        Collection<Item> answers = itemRepository.findAllByRequestId(requestId);

        return itemRequestRepository.findById(requestId)
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, answers))
                .orElseThrow(() -> new NotFoundException("Запрос с ID: %s не найден".formatted(requestId)));
    }

    private User throwIfUserNotExist(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }
}
