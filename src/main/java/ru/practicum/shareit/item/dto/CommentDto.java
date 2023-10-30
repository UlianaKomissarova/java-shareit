package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotEmpty
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;
}