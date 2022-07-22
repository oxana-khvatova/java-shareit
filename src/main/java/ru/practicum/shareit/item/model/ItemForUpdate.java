package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Size;

@Data
public class ItemForUpdate {
    private Long id;
    private String name;
    @Size(max = 300)
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
}