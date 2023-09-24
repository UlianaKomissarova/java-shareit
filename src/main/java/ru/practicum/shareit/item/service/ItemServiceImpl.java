package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item add(Integer userId, ItemDto dto) {
        return itemStorage.add(userId, dto);
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto dto) {
        return itemStorage.update(userId, itemId, dto);
    }

    @Override
    public Item get(Integer userId, Integer itemId) {
        return itemStorage.get(userId, itemId);
    }

    @Override
    public Collection<Item> getAll(Integer userId) {
        return itemStorage.getAll(userId);
    }

    @Override
    public Collection<Item> search(Integer userId, String text) {
        return itemStorage.search(userId, text);
    }
}