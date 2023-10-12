package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.RequestMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    public static final Sort SORT = Sort.by("created").descending();

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestDto dto) {
        User user = getExistingUser(userId);

        ItemRequest request = toRequest(dto);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        return toRequestDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        getExistingUser(userId);
        ItemRequest request = getExistingRequest(requestId);
        ItemRequestDto result = toRequestDto(request);
        fillRequestsWithItems(result);

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemRequestDto> findAll(Long userId) {
        getExistingUser(userId);

        List<ItemRequestDto> requests = requestRepository.findByRequesterId(userId, SORT)
            .stream()
            .map(RequestMapper::toRequestDto)
            .collect(Collectors.toList());

        for (ItemRequestDto request : requests) {
            fillRequestsWithItems(request);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemRequestDto> findAllFromOtherUsers(Long userId, Integer from, Integer size) {
        validatePagination(from, size);
        getExistingUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT);

        List<ItemRequestDto> requests = requestRepository.findByRequesterIdIsNot(userId, pageable)
            .stream()
            .map(RequestMapper::toRequestDto)
            .collect(Collectors.toList());

        for (ItemRequestDto request : requests) {
            fillRequestsWithItems(request);
        }

        return requests;
    }

    private void validatePagination(Integer from, Integer size) {
        if (from < 0 || size < 0) {
            throw new ItemRequestBadRequestException("Параметры пагинации должны быть положительными.");
        }
    }

    private User getExistingUser(long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new UserNotFoundException("Пользователь с id " + id + " не найден.")
        );
    }

    private ItemRequest getExistingRequest(long id) {
        return requestRepository.findById(id).orElseThrow(
            () -> new RequestNotFoundException("Запрос с id " + id + " не найден.")
        );
    }

    private void fillRequestsWithItems(ItemRequestDto request) {
        List<ItemDtoInRequest> items = itemRepository
            .findByRequestId(request.getId())
            .stream().map(ItemMapper::toItemDtoInRequest)
            .collect(Collectors.toList());

        if (!items.isEmpty()) {
            request.setItems(items);
        } else {
            request.setItems(new ArrayList<>());
        }
    }
}