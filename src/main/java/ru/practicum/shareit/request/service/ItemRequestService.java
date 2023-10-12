package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto save(Long userId, ItemRequestDto dto);

    ItemRequestDto findById(Long userId, Long requestId);

    Collection<ItemRequestDto> findAll(Long userId);

    Collection<ItemRequestDto> findAllFromOtherUsers(Long userId, Integer from, Integer size);
}