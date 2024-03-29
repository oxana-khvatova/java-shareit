package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NonNull
    @NotBlank
    private String text;
    private Long authorId;
    private Long itemId;
    private LocalDateTime created;
}