package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {
    BookingRepository bookingRepository;
    ItemService itemService;

    UserService userService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    public Booking save(Booking booking, Long userId) {
        Item item = itemService.findById(booking.getItemId());
        if (!item.getAvailable() || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingIsImpossible(" Booking is impossible");
        }
        if (item.getOwner().equals(userId)) {
            throw new ItemNotFoundException(" Booking your own thing is not possible");
        }
        userService.findById(userId);
        booking.setStatus(Status.WAITING);
        booking.setBookerId(userId);
        return bookingRepository.save(booking);
    }

    public Booking upDateStatus(Long userId, Long bookingId, String approved) {
        Booking booking = findById(bookingId);
        Item item = itemService.findById(booking.getItemId());
        if (Objects.equals(item.getOwner(), userId)) {
            if (approved.equals("true") && booking.getStatus().equals(Status.WAITING)) {
                booking.setStatus(Status.APPROVED);
            } else if (approved.equals("false") && booking.getStatus().equals(Status.WAITING)) {
                booking.setStatus(Status.REJECTED);
            } else {
                throw new BookingIsImpossible("Bad request");
            }
        } else {
            throw new UserNotFoundException("Change status allowed only for owner");
        }
        return bookingRepository.save(booking);
    }

    public Booking findById(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else {
            throw new BookingNotFoundException(" Booking is impossible");
        }
    }

    public Booking getBooking(Long userId, Long bookingId) {
        Booking booking = findById(bookingId);
        Item item = itemService.findById(booking.getItemId());
        if (booking.getBookerId().equals(userId) || item.getOwner().equals(userId)) {
            return booking;
        } else {
            throw new ItemNotFoundException("No access");
        }
    }

    public List<Booking> getAllBookingForUser(Long bookerId) {
        return bookingRepository.findByBookerId(bookerId).stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
    }

    public List<Booking> getAllBookingForOwner(Long ownerId) {
        return bookingRepository.findByOwnerId(ownerId).stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
    }
}