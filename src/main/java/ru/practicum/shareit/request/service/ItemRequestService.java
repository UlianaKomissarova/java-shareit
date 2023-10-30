package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.core.exception.exceptions.RequestNotFoundException;
import ru.practicum.shareit.core.validation.PaginationValidator;
import ru.practicum.shareit.item.dto.ItemDtoInRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.dto.RequestMapper.*;

@Service
@RequiredArgsConstructor
public class ItemRequestService implements ItemRequestServiceInterface {
    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;
    public static final Sort SORT = Sort.by("created").descending();

    @Transactional
    @Override
    public ItemRequestDto save(Long userId, ItemRequestDto dto) {
        User user = userService.getExistingUser(userId);

        ItemRequest request = toRequest(dto);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);

        return toRequestDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        userService.getExistingUser(userId);
        ItemRequest request = getExistingRequest(requestId);
        ItemRequestDto result = toRequestDto(request);
        fillRequestsWithItems(result);

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemRequestDto> findAll(Long userId) {
        userService.getExistingUser(userId);
        List<ItemRequest> requests = requestRepository.findByRequesterId(userId, SORT);

        return mapListToDtoList(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ItemRequestDto> findAllFromOtherUsers(Long userId, Integer from, Integer size) {
        PaginationValidator.validatePagination(from, size);
        userService.getExistingUser(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT);
        List<ItemRequest> requests = requestRepository.findByRequesterIdIsNot(userId, pageable);

        return mapListToDtoList(requests);
    }

    private List<ItemRequestDto> mapListToDtoList(List<ItemRequest> requests) {
        List<ItemRequestDto> result = requests.stream()
            .map(RequestMapper::toRequestDto)
            .collect(Collectors.toList());

        for (ItemRequestDto request : result) {
            fillRequestsWithItems(request);
        }

        return result;
    }

    public ItemRequest getExistingRequest(long id) {
        return requestRepository.findById(id).orElseThrow(
            () -> new RequestNotFoundException("Запрос с id " + id + " не найден.")
        );
    }

    private void fillRequestsWithItems(ItemRequestDto request) {
        List<ItemDtoInRequest> items = itemService.getItemsByRequestId(request.getId());

        if (!items.isEmpty()) {
            request.setItems(items);
        } else {
            request.setItems(new ArrayList<>());
        }
    }
}