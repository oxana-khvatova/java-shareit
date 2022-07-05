package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserDto {
    @Pattern(regexp = "^\\S+$", message = "не должен содержать пробелы")
    private String name;
    private Long idUser;
}