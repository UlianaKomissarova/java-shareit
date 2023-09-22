package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
@AllArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}