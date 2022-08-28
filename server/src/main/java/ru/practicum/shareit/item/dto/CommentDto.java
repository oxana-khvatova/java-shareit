package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NonNull
    @NotBlank
    private String text;
    @NotNull
    private Long authorId;
    @NotNull
    private Long itemId;
    private LocalDateTime created;
}