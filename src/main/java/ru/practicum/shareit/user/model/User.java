package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class User {
    private Long id;
    @NotNull
    @Pattern(regexp = "^\\S+$", message = "не должен содержать пробелы")
    private String name;
    @NotNull
    @NotBlank
    @Email
    private String email;
}