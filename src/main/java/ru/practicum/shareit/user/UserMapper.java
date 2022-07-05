package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Data
@AllArgsConstructor
public class UserMapper {
    private UserDto toUserDto(User user) {
        return new UserDto(user.getName(), user.getId());
    }
}