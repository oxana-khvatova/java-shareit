package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Item {
    private Long id;
    @NotBlank
    @NotNull
    private String name;
    @NotNull
    @Size(max = 300)
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}