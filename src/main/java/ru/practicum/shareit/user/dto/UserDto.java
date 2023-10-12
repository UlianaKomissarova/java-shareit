package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;

@Getter
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    @Email
    @NotNull
    private String email;
}