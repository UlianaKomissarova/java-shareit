package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class ItemDtoInRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}