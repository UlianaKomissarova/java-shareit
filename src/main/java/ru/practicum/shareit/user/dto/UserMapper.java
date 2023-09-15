package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
            user.getName(),
            user.getEmail()
        );
    }

    public static User toUser(Integer id, UserDto dto) {
        return new User(
            id,
            dto.getName(),
            dto.getEmail()
        );
    }
}