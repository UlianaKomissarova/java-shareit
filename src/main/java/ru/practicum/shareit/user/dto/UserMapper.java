package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }

    public static User toUser(UserDto dto) {
        return User.builder()
            .id(dto.getId())
            .name(dto.getName())
            .email(dto.getEmail())
            .build();
    }
}