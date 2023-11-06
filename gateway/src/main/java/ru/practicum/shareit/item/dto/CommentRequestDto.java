package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotEmpty
    @NotNull
    private String text;
}