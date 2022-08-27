package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
public class UserForUpdate {
    private Long id;
    @Pattern(regexp = "^\\S+$", message = "не должен содержать пробелы")
    private String name;
    @Email
    private String email;
}
