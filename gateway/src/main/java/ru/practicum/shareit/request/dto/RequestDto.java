package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto {
    @NotNull
    @NotBlank
    private String description;
}