package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForAdd;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingIsImpossible;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class BookingServiceTest {
    static User booker;
    static User owner;
    static Item item;
    static BookingForAdd booking;
    static Booking bookingNew;
    static BookingRepository mockBookingRepository;
    static ItemService mockItemService;
    static UserService mockUserService;
    static BookingService bookingService;

    @BeforeEach
    public void init() {
        owner = new User();
        owner.setName("Ivan");
        owner.setEmail("email@com.ru");
        owner.setId(1L);

        booker = new User();
        booker.setName("Ivan");
        booker.setEmail("email@com.ru");
        booker.setId(10L);

        item = new Item();
        item.setId(11L);
        item.setName("Table");
        item.setDescription("Big table");
        item.setAvailable(true);
        item.setOwner(owner.getId());

        booking = new BookingForAdd();
        booking.setId(12L);
        booking.setStart(LocalDateTime.of(2022, 9, 2, 11, 10, 15));
        booking.setEnd(LocalDateTime.of(2022, 9, 3, 11, 10, 15));
        booking.setItemId(item.getId());
        booking.setBookerId(booker.getId());
        booking.setStatus(Status.WAITING);

        bookingNew = new Booking();
        bookingNew.setId(13L);
        bookingNew.setStart(LocalDateTime.of(2022, 9, 5, 11, 10, 15));
        bookingNew.setEnd(LocalDateTime.of(2022, 9, 6, 11, 10, 15));
        bookingNew.setItemId(item.getId());
        bookingNew.setBookerId(booker.getId());
        bookingNew.setStatus(Status.WAITING);

        mockItemService = Mockito.mock(ItemService.class);
        mockUserService = Mockito.mock(UserService.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        bookingService = new BookingService(mockBookingRepository, mockItemService, mockUserService,
                new BookingMapper(mockItemService, mockUserService));
    }

    @Test
    void shouldCreateBookingAndBookingIsImpossibleException() {
        Mockito
                .when(mockItemService.findById(booking.getItemId()))
                .thenReturn(item);
        Mockito
                .when(mockUserService.findById(booking.getBookerId()))
                .thenReturn(booker);
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                            Booking b = invocationOnMock.getArgument(0, Booking.class);
                            b.setId(booking.getId());
                            return b;
                        }
                );
        BookingDto returnedBooking = bookingService.save(booking, booker.getId());
        Assertions.assertEquals(booking.getId(), returnedBooking.getId());
        Assertions.assertEquals(booking.getStart(), returnedBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), returnedBooking.getEnd());
        Assertions.assertEquals(booking.getItemId(), returnedBooking.getItem().getId());
        Assertions.assertEquals(booking.getBookerId(), returnedBooking.getBooker().getId());
        Assertions.assertEquals(booking.getStatus(), returnedBooking.getStatus());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            bookingService.save(booking, owner.getId());
        });

        booking.setEnd(LocalDateTime.of(2021, 9, 3, 11, 10, 15));
        Assertions.assertThrows(BookingIsImpossible.class, () -> {
            bookingService.save(booking, owner.getId());
        });
    }

    @Test
    void shouldUpDateBookingAndBookingIsImpossibleExceptionBookingNotFoundException() {
        Mockito
                .when(mockItemService.findById(booking.getItemId()))
                .thenReturn(item);
        Mockito
                .when(mockBookingRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> {
                            Booking b = invocationOnMock.getArgument(0, Booking.class);
                            b.setId(bookingNew.getId());
                            return b;
                        }
                );
        Mockito
                .when(mockBookingRepository.findById(bookingNew.getId()))
                .thenReturn(Optional.of(bookingNew));
        Mockito
                .when(mockUserService.findById(bookingNew.getBookerId()))
                .thenReturn(booker);

        BookingDto returnedBooking = bookingService.upDateStatus(owner.getId(), bookingNew.getId(), "true");
        Assertions.assertEquals(bookingNew.getId(), returnedBooking.getId());
        Assertions.assertEquals(bookingNew.getStart(), returnedBooking.getStart());
        Assertions.assertEquals(bookingNew.getEnd(), returnedBooking.getEnd());
        Assertions.assertEquals(bookingNew.getItemId(), returnedBooking.getItem().getId());
        Assertions.assertEquals(bookingNew.getBookerId(), returnedBooking.getBooker().getId());
        Assertions.assertEquals(bookingNew.getStatus(), returnedBooking.getStatus());

        Assertions.assertThrows(BookingIsImpossible.class, () -> {
            bookingService.upDateStatus(owner.getId(), bookingNew.getId(), "sss");
        });

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            bookingService.upDateStatus(owner.getId() + 1, bookingNew.getId(), "true");
        });

        Assertions.assertThrows(BookingNotFoundException.class, () -> {
            bookingService.upDateStatus(owner.getId(), bookingNew.getId() + 1, "true");
        });
    }

    @Test
    void shouldGetBookingAndItemNotFoundException() {
        Mockito
                .when(mockBookingRepository.findById(bookingNew.getId()))
                .thenReturn(Optional.of(bookingNew));
        Mockito
                .when(mockItemService.findById(bookingNew.getItemId()))
                .thenReturn(item);
        Mockito
                .when(mockUserService.findById(bookingNew.getBookerId()))
                .thenReturn(booker);

        BookingDto returnedBooking = bookingService.getBooking(booker.getId(), bookingNew.getId());
        Assertions.assertEquals(bookingNew.getId(), returnedBooking.getId());
        Assertions.assertEquals(bookingNew.getStart(), returnedBooking.getStart());
        Assertions.assertEquals(bookingNew.getEnd(), returnedBooking.getEnd());
        Assertions.assertEquals(bookingNew.getItemId(), returnedBooking.getItem().getId());
        Assertions.assertEquals(bookingNew.getBookerId(), returnedBooking.getBooker().getId());
        Assertions.assertEquals(bookingNew.getStatus(), returnedBooking.getStatus());

        Assertions.assertThrows(ItemNotFoundException.class, () -> {
            bookingService.getBooking(booker.getId() + 1, bookingNew.getId());
        });
    }

    @Test
    void shouldGetAllBookingForUser() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNew);

        Mockito
                .when(mockBookingRepository.findByBookerId(Mockito.anyLong()))
                .thenReturn(bookings);
        Mockito
                .when(mockUserService.findById(bookingNew.getBookerId()))
                .thenReturn(booker);
        Mockito
                .when(mockItemService.findById(bookingNew.getItemId()))
                .thenReturn(item);

        List<BookingDto> returnBooking = bookingService.getAllBookingForUser(booker.getId());
        BookingDto returnFirst = returnBooking.get(0);
        Booking booking1 = bookings.get(0);
        Assertions.assertEquals(booking1.getId(), returnFirst.getId());
        Assertions.assertEquals(booking1.getStart(), returnFirst.getStart());
        Assertions.assertEquals(booking1.getEnd(), returnFirst.getEnd());
        Assertions.assertEquals(booking1.getItemId(), returnFirst.getItem().getId());
        Assertions.assertEquals(booking1.getBookerId(), returnFirst.getBooker().getId());
        Assertions.assertEquals(booking1.getStatus(), returnFirst.getStatus());
    }

    @Test
    void shouldGetAllBookingForOwner() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingNew);

        Mockito
                .when(mockBookingRepository.findByOwnerId(Mockito.anyLong()))
                .thenReturn(bookings);
        Mockito
                .when(mockItemService.findById(bookingNew.getItemId()))
                .thenReturn(item);
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(booker);
        List<BookingDto> returnBooking = bookingService.getAllBookingForOwner(owner.getId());
        BookingDto returnFirst = returnBooking.get(0);
        Booking booking1 = bookings.get(0);
        Assertions.assertEquals(booking1.getId(), returnFirst.getId());
        Assertions.assertEquals(booking1.getStart(), returnFirst.getStart());
        Assertions.assertEquals(booking1.getEnd(), returnFirst.getEnd());
        Assertions.assertEquals(booking1.getItemId(), returnFirst.getItem().getId());
        Assertions.assertEquals(booking1.getBookerId(), returnFirst.getBooker().getId());
        Assertions.assertEquals(booking1.getStatus(), returnFirst.getStatus());
    }
}
