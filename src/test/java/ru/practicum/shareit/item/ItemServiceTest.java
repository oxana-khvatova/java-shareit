package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingIsImpossible;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperForOwner;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemForUpdate;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ItemServiceTest {
    static Item item;
    static User owner;
    static User booker;
    static ItemForUpdate updateItem;
    static ItemRepository mockItemRepository;
    static UserRepository mockUserRepository;
    static CommentRepository mockCommentRepository;
    static BookingRepository mockBookingRepository;
    static ItemService itemService;
    static ItemRequest itemRequest;
    static Booking booking;

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

        itemRequest = new ItemRequest();
        itemRequest.setId(3L);
        itemRequest.setDescription("Table");
        itemRequest.setUserRequesterId(2L);
        itemRequest.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(10L);
        item.setName("Table");
        item.setDescription("Big table");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item.setRequest(itemRequest.getId());

        updateItem = new ItemForUpdate();
        updateItem.setId(10L);
        updateItem.setName("Chair");
        updateItem.setDescription("Small chair");
        updateItem.setAvailable(false);
        updateItem.setOwner(owner);
        updateItem.setRequest(itemRequest);

        booking = new Booking();
        booking.setId(12L);
        booking.setStart(LocalDateTime.of(2022, 3, 2, 11, 10, 15));
        booking.setEnd(LocalDateTime.of(2022, 4, 3, 11, 10, 15));
        booking.setItemId(item.getId());
        booking.setBookerId(booker.getId());
        booking.setStatus(Status.APPROVED);

        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);
        mockCommentRepository = Mockito.mock(CommentRepository.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        CommentMapper commentMapper = new CommentMapper(mockUserRepository);

        itemService = new ItemService(mockItemRepository, mockUserRepository,
                mockCommentRepository, mockBookingRepository, new ItemMapper(commentMapper),
                new ItemMapperForOwner(mockBookingRepository, commentMapper));
    }

    @Test
    void shouldCreateItemAndUserNotFoundException() {
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(mockUserRepository.findById(2L))
                .thenReturn(Optional.empty());
        Item returnedItem = itemService.save(item, owner.getId());
        Assertions.assertEquals(returnedItem, item);
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            itemService.save(item, 2L);
        });
    }

    @Test
    void shouldUpdateItemAndForbiddenException() {
        Mockito
                .when(mockItemRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Item.class));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));

        ItemDto returnedItem = itemService.upDate(updateItem, owner.getId(), item.getId());
        Assertions.assertEquals(returnedItem.getId(), updateItem.getId());
        Assertions.assertEquals(returnedItem.getName(), updateItem.getName());
        Assertions.assertEquals(returnedItem.getDescription(), updateItem.getDescription());
        Assertions.assertEquals(returnedItem.getAvailable(), updateItem.getAvailable());
        Assertions.assertEquals(returnedItem.getIdOwner(), updateItem.getOwner().getId());
        Assertions.assertEquals(returnedItem.getIdItemRequest(), updateItem.getRequest().getId());

        Assertions.assertThrows(ForbiddenAccessException.class, () -> {
            itemService.upDate(updateItem, owner.getId() + 1, item.getId());
        });
    }

    @Test
    void shouldFondAll() {
        List<Item> items = new ArrayList<>();
        items.add(item);
        Mockito
                .when(mockItemRepository.findAll())
                .thenReturn(items);
        Assertions.assertEquals(itemService.findAll(), items);
    }

    @Test
    void shouldFindAllOwnerItems() {
        Item newItem = new Item();
        newItem.setId(5L);
        item.setName("Lamp");
        item.setDescription("new lamp");
        item.setAvailable(false);
        item.setOwner(owner.getId());

        List<Item> items = new ArrayList<>();
        items.add(item);
        items.add(newItem);

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, items.size(), sortById);
        Mockito
                .when(mockItemRepository.findByOwner(owner.getId(), page))
                .thenReturn(items);
        Assertions.assertEquals(items, itemService.findAllOwnerItems(owner.getId(), 0, items.size()));
    }

    @Test
    void shouldSearch() {
        List<Item> items = new ArrayList<>();
        items.add(item);

        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        Pageable page = PageRequest.of(0, items.size(), sortById);
        Mockito
                .when(mockItemRepository.search("Tab", page))
                .thenReturn(items);
        Assertions.assertEquals(itemService.search("Tab", 0, items.size()), items);
        Assertions.assertEquals(itemService.search("  ", 0, items.size()), new ArrayList<>());
    }

    @Test
    void shouldAddCommentsAndBookingIsImpossibleException() {
        Comments comment = new Comments();
        comment.setId(13L);
        comment.setText("Good table");
        comment.setItemId(item.getId());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorId(booker.getId());

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);

        Mockito
                .when(mockBookingRepository.findByItemId(item.getId()))
                .thenReturn(bookingList);
        Mockito
                .when(mockCommentRepository.save(Mockito.any()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0, Comments.class));
        Mockito
                .when(mockItemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        Assertions.assertEquals(comment, itemService.addComments(booker.getId(), item.getId(), comment));
        Assertions.assertThrows(BookingIsImpossible.class, () -> {
            itemService.addComments(owner.getId(), item.getId(), comment);
        });
    }
}
