package ru.practicum.shareit.booking.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingForUpdate {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
}