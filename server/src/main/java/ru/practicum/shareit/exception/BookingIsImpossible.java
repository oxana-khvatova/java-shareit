package ru.practicum.shareit.exception;

public class BookingIsImpossible extends RuntimeException {
    public BookingIsImpossible(String message) {
        super(message);
    }
}