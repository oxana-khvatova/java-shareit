package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class BookingDto {
    @Data
    @AllArgsConstructor
    public static class Item {
        @NonNull
        @NotBlank
        private String name;
        @Size(max = 300)
        private String description;
        @NonNull
        private Boolean available;

    }

    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private Status status;
}