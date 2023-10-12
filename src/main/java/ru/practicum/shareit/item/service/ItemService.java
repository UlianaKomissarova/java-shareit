package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto save(Long userId, ItemDto dto);

    ItemDto update(Long userId, Long itemId, ItemDto dto);

    ItemDto findById(Long userId, Long itemId);

    Collection<ItemDto> findAll(Long userId, int from, int size);

    Collection<ItemDto> search(Long userId, String text, int from, int size);

    CommentDto saveComment(Long userId, Long itemId, CommentDto dto);
}