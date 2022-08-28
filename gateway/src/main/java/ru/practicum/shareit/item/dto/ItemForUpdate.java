package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ItemForUpdate {
    private Long id;
    private String name;
    @Size(max = 300)
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
}