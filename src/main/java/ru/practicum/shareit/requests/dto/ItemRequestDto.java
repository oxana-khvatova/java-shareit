package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    @NotNull
    private Long id;
    @Size(max = 300)
    private String description;
    @NotNull
    private Long requesterId;
    private LocalDateTime created;
}