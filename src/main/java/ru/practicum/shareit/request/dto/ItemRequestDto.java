package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoInRequest;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotNull
    @NotBlank
    private String description;
    private LocalDateTime created;
    private List<ItemDtoInRequest> items;
}