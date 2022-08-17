package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingForAdd;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class RequestsServiceTest {
    static User requester;
    static Item item;
    static RequestRepository mockRequestRepository;
    static UserService mockUserService;
    static RequestService requestService;
    static ItemRequest itemRequest;

    @BeforeEach
    public void init() {
        requester = new User();
        requester.setName("Ivan");
        requester.setEmail("email@com.ru");
        requester.setId(10L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Lamp");
        itemRequest.setUserRequesterId(requester.getId());
        itemRequest.setCreated(LocalDateTime.now());

        mockRequestRepository = Mockito.mock(RequestRepository.class);
        mockUserService = Mockito.mock(UserService.class);
        requestService = new RequestService(mockRequestRepository, new ItemRequestMapper(), mockUserService);
    }

    @Test
    void shouldCreateRequest() {
        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(requester);
        Mockito
                .when(mockRequestRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, ItemRequest.class));

        ItemRequestDto returnRequest = requestService.save(itemRequest, requester.getId());
        Assertions.assertNotNull(returnRequest.getCreated());
        Assertions.assertEquals(itemRequest.getUserRequesterId(), returnRequest.getIdRequest());
        Assertions.assertEquals(itemRequest.getDescription(), returnRequest.getDescription());
    }

    @Test
    void shouldGetAllUserRequest() {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);

        Mockito
                .when(mockUserService.findById(Mockito.anyLong()))
                .thenReturn(requester);
        Mockito
                .when(mockRequestRepository.findByOwnerId(Mockito.anyLong()))
                .thenReturn(itemRequestList);

        List<ItemRequestDto> returnListRequest = requestService.getAllUserRequest(requester.getId());
        ItemRequestDto returnFirst = returnListRequest.get(0);
        ItemRequest itemRequest1 = itemRequestList.get(0);
        Assertions.assertEquals(itemRequest1.getCreated(), returnFirst.getCreated());
        Assertions.assertEquals(itemRequest1.getDescription(), returnFirst.getDescription());
        Assertions.assertEquals(itemRequest1.getUserRequesterId(), returnFirst.getIdRequest());
    }

    @Test
    void shouldFindAllRequest() {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        itemRequestList.add(itemRequest);
        Page<ItemRequest> page = new PageImpl<>(itemRequestList);

        Sort sortById = Sort.by(Sort.Direction.ASC, "created");
        Pageable pageable = PageRequest.of(0, itemRequestList.size(), sortById);
        Mockito
                .when(mockRequestRepository.findAll(pageable))
                .thenReturn(page);
        List<ItemRequestDto> returnItemRequest = requestService.findAllRequest( 0, itemRequestList.size());
        ItemRequestDto returnFirst = returnItemRequest.get(0);
        ItemRequest itemRequest1 = itemRequestList.get(0);
        Assertions.assertEquals(itemRequest1.getCreated(), returnFirst.getCreated());
        Assertions.assertEquals(itemRequest1.getDescription(), returnFirst.getDescription());
        Assertions.assertEquals(itemRequest1.getUserRequesterId(), returnFirst.getIdRequest());
    }

    @Test
    void shouldПetRequest() {
        Mockito
                .when(mockRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(itemRequest));
        ItemRequestDto returnItemRequest = requestService.getRequest(itemRequest.getId());
        Assertions.assertEquals(itemRequest.getCreated(), returnItemRequest.getCreated());
        Assertions.assertEquals(itemRequest.getDescription(), returnItemRequest.getDescription());
        Assertions.assertEquals(itemRequest.getUserRequesterId(), returnItemRequest.getIdRequest());
    }
}
