package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class ItemDto {
    @NonNull
    @NotBlank
    private String name;
    @Size(max = 300)
    private String description;
    @NonNull
    private Boolean available;
    private Long idItemRequest;
    private Long idOwner;
}