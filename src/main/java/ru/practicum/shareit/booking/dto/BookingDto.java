package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    @Data
    @AllArgsConstructor
    public static class Item {
        @NonNull
        private Long id;
        @NonNull
        @NotBlank
        private String name;
        @Size(max = 300)
        private String description;
        @NonNull
        private Boolean available;
    }

    @Data
    @AllArgsConstructor
    public static class Booker {
        @NonNull
        private Long id;
    }

    @NonNull
    private Long id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private Item item;
    @NotNull
    private Status status;
    private Booker booker;
}