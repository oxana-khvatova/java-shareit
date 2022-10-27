package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ItemDto {

    private Long id;
    @NonNull
    @NotBlank
    private String name;
    @Size(max = 300)
    private String description;
    @NonNull
    private Boolean available;
    private Long idItemRequest;
    private Long idOwner;
    private List<CommentDto> comments;
}
