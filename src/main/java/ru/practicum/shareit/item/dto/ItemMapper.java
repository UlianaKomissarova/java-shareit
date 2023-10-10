package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
            .id(item.getId())
            .name(item.getName())
            .description(item.getDescription())
            .available(item.getAvailable())
            .build();
    }

    public static Item toItem(ItemDto dto) {
        return Item.builder()
            .id(dto.getId())
            .name(dto.getName())
            .description(dto.getDescription())
            .available(dto.getAvailable())
            .build();
    }
}