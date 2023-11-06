package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

@Component
public class RequestMapper {
    public static ItemRequest toRequest(ItemRequestDto dto) {
        return ItemRequest.builder()
            .id(dto.getId())
            .description(dto.getDescription())
            .created(dto.getCreated())
            .build();
    }

    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
            .id(request.getId())
            .description(request.getDescription())
            .created(request.getCreated())
            .build();
    }
}