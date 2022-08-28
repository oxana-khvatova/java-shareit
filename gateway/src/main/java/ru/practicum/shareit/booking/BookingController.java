package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingForAdd;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @Autowired
    public BookingController(BookingClient bookingService) {
        this.bookingClient = bookingService;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody BookingForAdd booking) {
        log.info("Add booking " + booking);
        return bookingClient.bookItem(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> upDateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam String approved) {
        log.info("upDateStatus for bookingId " + bookingId);
        return bookingClient.upDateStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long bookingId) {
        log.info("get bookingId " + bookingId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingForBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("get listBooking  for user " + bookerId);
        return bookingClient.getAllBookingForBooker(bookerId,state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingForOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingClient.getAllBookingForOwner(ownerId,state);
    }
}
