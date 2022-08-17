package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForAdd;
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
@Slf4j
public class BookingService {
    BookingRepository bookingRepository;
    ItemService itemService;
    UserService userService;
    BookingMapper bookingMapper;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ItemService itemService,
                          UserService userService, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
        this.bookingMapper = bookingMapper;
    }

    public BookingDto save(BookingForAdd booking, Long userId) {
        Item item = itemService.findById(booking.getItemId());
        if (!item.getAvailable() || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart())) {
            log.error("Нарушены условия для бронирования");
            throw new BookingIsImpossible(" Booking is impossible");
        }
        if (item.getOwner().equals(userId)) {
            log.error("Бронирование собственных вещей невозможно");
            throw new ItemNotFoundException(" Booking your own thing is not possible");
        }
        userService.findById(userId);
        Booking bookingForRepository = bookingMapper.toBooking(booking);
        bookingForRepository.setStatus(Status.WAITING);
        bookingForRepository.setBookerId(userId);
        Booking bookingWithId = bookingRepository.save(bookingForRepository);
        return bookingMapper.toBookingDto(bookingWithId);
    }

    public BookingDto upDateStatus(Long userId, Long bookingId, String approved) {
        Booking booking = findById(bookingId);
        Item item = itemService.findById(booking.getItemId());
        if (Objects.equals(item.getOwner(), userId)) {
            if (approved.equals("true") && booking.getStatus().equals(Status.WAITING)) {
                log.info("Бронирование подтверждено");
                booking.setStatus(Status.APPROVED);
            } else if (approved.equals("false") && booking.getStatus().equals(Status.WAITING)) {
                log.info("Бронирование отклонено");
                booking.setStatus(Status.REJECTED);
            } else {
                log.error("Такой команды нет");
                throw new BookingIsImpossible("Bad request");
            }
        } else {
            log.error("Возможность менять статус есть только у владельца");
            throw new UserNotFoundException("Change status allowed only for owner");
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public Booking findById(Long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isPresent()) {
            return booking.get();
        } else {
            log.error("Бронирование невозможно, вещь недоступна");
            throw new BookingNotFoundException(" Booking is impossible");
        }
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = findById(bookingId);
        Item item = itemService.findById(booking.getItemId());
        if (booking.getBookerId().equals(userId) || item.getOwner().equals(userId)) {
            return bookingMapper.toBookingDto(booking);
        } else {
            log.info("Нет доступа к просмотру");
            throw new ItemNotFoundException("No access");
        }
    }

    public List<BookingDto> getAllBookingForUser(Long bookerId) {
 //       userService.findById(bookerId);
        List<Booking> bookingList = bookingRepository.findByBookerId(bookerId).stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
        return bookingMapper.toBookingDtoList(bookingList);
    }

    public List<BookingDto> getAllBookingForOwner(Long ownerId) {
        List<Booking> bookingList = bookingRepository.findByOwnerId(ownerId).stream()
                .sorted((o1, o2) -> o2.getStart().compareTo(o1.getStart())).collect(Collectors.toList());
        return bookingMapper.toBookingDtoList(bookingList);
    }
}