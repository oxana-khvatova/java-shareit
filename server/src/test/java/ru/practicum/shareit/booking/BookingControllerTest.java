package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForAdd;
import ru.practicum.shareit.booking.dto.Status;
import ru.practicum.shareit.exception.BookingIsImpossible;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @MockBean
    BookingMapper bookingMapper;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public BookingControllerTest(BookingService bookingService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.bookingService = bookingService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    static BookingForAdd booking;
    static User booker;
    static Item item;
    static BookingDto bookingDto;
    static BookingDto.Item bookingDtoItem;
    static BookingDto.Booker bookingDtoBooker;

    @BeforeEach
    public void init() {
        booker = new User();
        booker.setName("Ivan");
        booker.setEmail("email@com.ru");
        booker.setId(10L);

        item = new Item();
        item.setId(11L);
        item.setName("Table");
        item.setDescription("Big table");
        item.setAvailable(true);

        booking = new BookingForAdd();
        booking.setId(12L);
        booking.setStart(LocalDateTime.of(2022, 9, 2, 11, 10, 15));
        booking.setEnd(LocalDateTime.of(2022, 9, 3, 11, 10, 15));
        booking.setItemId(item.getId());
        booking.setBookerId(booker.getId());
        booking.setStatus(Status.WAITING);

        bookingDtoItem = new BookingDto.Item(2L, "Lamp", "New", true);
        bookingDtoBooker = new BookingDto.Booker(3L);

        bookingDto = new BookingDto(
                1L,
                LocalDateTime.of(2022, 9, 2, 11, 10, 15),
                LocalDateTime.of(2022, 9, 3, 11, 10, 15),
                bookingDtoItem,
                Status.WAITING, bookingDtoBooker);
    }

    @Test
    public void createBooking_thenStatus200andBookingReturned() throws Exception {
        Mockito.when(bookingService.save(Mockito.any(), eq(bookingDtoBooker.getId()))).thenReturn(bookingDto);
        mockMvc.perform(
                        post("/bookings")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(booking))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    public void createBookingForUnavailable() throws Exception {
        Mockito.when(bookingService.save(Mockito.any(), eq(bookingDtoBooker.getId())))
                        .thenThrow(new BookingIsImpossible("impossible"));
        mockMvc.perform(
                        post("/bookings")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(booking))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void upDateBooking_thenStatus200andBookingReturned() throws Exception {
        Mockito.when(bookingService.upDateStatus(Mockito.anyLong(), eq(booking.getId()), Mockito.anyString()))
                .thenReturn(bookingDto);
        String approved = "true";
        mockMvc.perform(
                        patch("/bookings/{bookingId}", booking.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(booking))
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("approved", approved)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    public void getBooking_thenStatus200andReturned() throws Exception {
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingDto);
        mockMvc.perform(
                        get("/bookings/{bookingId}", booking.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()));
    }

    @Test
    public void getUnknownBooking_thenStatus404andReturned() throws Exception {
        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong())).thenThrow(
                BookingNotFoundException.class
        );
        mockMvc.perform(
                        get("/bookings/{bookingId}", booking.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllBookingForOwner_thenStatus200() throws Exception {
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);

        Mockito.when(bookingService.getAllBookingForOwner(Mockito.anyLong())).thenReturn(bookings);
        String params = "all";
        mockMvc.perform(
                        get("/bookings/owner")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("params", params)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getAllBookingForBooker_thenStatus200() throws Exception {
        List<BookingDto> bookings = new ArrayList<>();
        bookings.add(bookingDto);

        Mockito.when(bookingService.getAllBookingForUser(Mockito.anyLong())).thenReturn(bookings);
        String params = "all";
        mockMvc.perform(
                        get("/bookings")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("params", params)
                                .header("X-Sharer-User-Id", bookingDto.getBooker().getId())
                )
                .andExpect(status().isOk());
    }
}
