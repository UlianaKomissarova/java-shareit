package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final UserStorage userStorage;
    private final HashMap<Integer, Item> items = new HashMap<>();
    private static int itemId = 0;
    @Override
    public Item add(Integer userId, ItemDto dto) {
        validateUser(userId);
        validateItemData(dto);

        Integer id = ++itemId;
        Item item = ItemMapper.toItem(id, userId, dto);
        items.put(id, item);

        return item;
    }

    @Override
    public Item update(Integer userId, Integer itemId, ItemDto dto) {
        validateUser(userId);

        Item item = validateItemOwner(userId);
        updateItemData(item, dto);
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item get(Integer userId, Integer itemId) {
        validateUser(userId);

        if (!items.containsKey(itemId)) {
            throw new ItemNotFoundException("Вещь с id " + itemId + " не найдена.");
        }

        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAll(Integer userId) {
        validateUser(userId);

        ArrayList<Item> userItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                userItems.add(item);
            }
        }

        return userItems;
    }

    @Override
    public Collection<Item> search(Integer userId, String text) {
        validateUser(userId);

        ArrayList<Item> itemsWithRequiredText = new ArrayList<>();

        if (!text.isEmpty()) {
            for (Item item : items.values()) {
                boolean doesNameContainText = item.getName().toLowerCase().contains(text.toLowerCase());
                boolean doesDescriptionContainText = item.getDescription().toLowerCase().contains(text.toLowerCase());
                if (item.getAvailable() && (doesNameContainText || doesDescriptionContainText)) {
                    itemsWithRequiredText.add(item);
                }
            }
        }

        return itemsWithRequiredText;
    }

    private void validateUser(Integer userId) {
        if (userId == null) {
            throw new UserNotFoundException("Не найден владелец/потенциальный арендатор вещи.");
        }

        userStorage.get(userId);
    }

    private void validateItemData(ItemDto dto) {
        if (dto.getName().isEmpty()) {
            throw new ItemValidationException("Название вещи не может быть пустым.");
        }

        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            throw new ItemValidationException("Описание вещи не может быть пустым.");
        }

        if (dto.getAvailable() == null) {
            throw new ItemValidationException("Статус вещи должен быть указан.");
        }
    }

    private Item validateItemOwner(Integer userId) {
        Item item = get(userId, itemId);
        if (!Objects.equals(item.getOwner(), userId)) {
            throw new ItemNotFoundException("Редактировать вещь может только её владелец.");
        }

        return item;
    }

    private void updateItemData(Item item, ItemDto dto) {
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
    }
}