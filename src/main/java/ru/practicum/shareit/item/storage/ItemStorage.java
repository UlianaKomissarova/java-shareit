package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item add(Integer userId, ItemDto dto);

    Item update(Integer userId, Integer itemId, ItemDto dto);

    Item get(Integer userId, Integer itemId);

    Collection<Item> getAll(Integer userId);

    Collection<Item> search(Integer userId, String text);
}