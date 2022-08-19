package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    @Size(max = 300)
    private String description;
    @NotNull
    private Long id;
    private LocalDateTime created;
}