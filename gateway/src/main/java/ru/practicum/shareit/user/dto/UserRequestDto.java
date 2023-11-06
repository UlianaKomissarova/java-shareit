package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private Long id;
    private String name;
    @Email
    @NotNull
    private String email;
}