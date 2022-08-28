package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {
    private Long id;
    @Pattern(regexp = "^\\S+$", message = "не должен содержать пробелы")
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    private String name;
}