package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @SpyBean
    ItemMapper itemMapper;
    @SpyBean
    CommentMapper commentMapper;
    @SpyBean
    ItemMapperForOwner itemMapperForOwner;
    @MockBean
    UserRepository userRepository;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    CommentRepository commentRepository;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public ItemControllerTest(ItemService itemService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.itemService = itemService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    static Item item;
    static ItemDto itemDto;
    static User owner;
    static ItemRequest itemRequest;
    static CommentDto commentsDto;
    static List<CommentDto> commentsListDto;
    static List<Comments> commentsList;
    static ItemForUpdate itemForUpdate;

    @BeforeEach
    public void init() {
        owner = new User();
        owner.setName("Ivan");
        owner.setEmail("email@com.ru");
        owner.setId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(3L);
        itemRequest.setDescription("Table");
        itemRequest.setUserRequesterId(2L);
        itemRequest.setCreated(LocalDateTime.now());

        commentsDto = new CommentDto(3L, "good", 1L,3L, LocalDateTime.now());
        commentsListDto = new ArrayList<>();
        commentsListDto.add(commentsDto);

        commentsList = new ArrayList<>();

        item = new Item();
        item.setId(10L);
        item.setName("Table");
        item.setDescription("Big table");
        item.setAvailable(true);
        item.setOwner(owner.getId());
        item.setRequest(itemRequest.getId());
        item.setComments(commentsList);

        itemForUpdate = new ItemForUpdate();
        itemForUpdate.setId(11L);
        itemForUpdate.setName("Table");
        itemForUpdate.setDescription("Big table");
        itemForUpdate.setAvailable(true);
        itemForUpdate.setOwner(owner);
        itemForUpdate.setRequest(itemRequest);

        itemDto = new ItemDto(11L, "Lamp", " ", true, itemRequest.getId(),
                owner.getId(), commentsListDto);
    }

    @Test
    public void createItem_thenStatus200andItemReturned() throws Exception {
        Mockito.when(itemService.save(Mockito.any(), eq(item.getOwner()))).thenReturn(item);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(owner));
        mockMvc.perform(
                        post("/items")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    public void upDateItem_thenStatus200andItemReturned() throws Exception {
        Mockito.when(itemService.upDate(Mockito.any(), Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(owner));
        mockMvc.perform(
                        patch("/items/{itemId}", item.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemForUpdate))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemForUpdate.getId()));
    }

    @Test
    public void getItem_thenStatus200() throws Exception {
        Mockito.when(itemService.findById(Mockito.anyLong())).thenReturn(item);
        mockMvc.perform(
                        get("/items/{itemId}", item.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    public void getItemNotOwner_thenStatus200() throws Exception {
        Mockito.when(itemService.findById(Mockito.anyLong())).thenReturn(item);
        mockMvc.perform(
                        get("/items/{itemId}", item.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 123)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()));
    }

    @Test
    public void getUnknownItem_thenStatus200() throws Exception {
        Mockito.when(itemService.findById(Mockito.anyLong()))
                .thenThrow(ItemNotFoundException.class);
        mockMvc.perform(
                        get("/items/{itemId}", item.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void createComment_thenStatus200andCommentReturned() throws Exception {
        Comments comments1 = new Comments();
        comments1.setAuthorId(1L);
        comments1.setCreated(LocalDateTime.now());
        comments1.setText("hello");
        comments1.setItemId(item.getId());

        Mockito.when(itemService.addComments(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(comments1);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(owner));
        mockMvc.perform(
                        post("/items/{itemId}/comment", item.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(comments1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comments1.getId()));
    }

    @Test
    public void getAllItem_thenStatus200() throws Exception {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Mockito.when(itemService.findAllOwnerItems(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemList);
        String from = "0";
        String size = "20";
        mockMvc.perform(
                        get("/items")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("from", from)
                                .param("size", size)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getItemSearch_thenStatus200() throws Exception {
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);

        Mockito.when(itemService.search(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemList);
        String from = "0";
        String size = "20";
        String text = "table";
        mockMvc.perform(
                        get("/items/search")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("text", text)
                                .param("from", from)
                                .param("size", size)
                                .header("X-Sharer-User-Id", item.getOwner())
                )
                .andExpect(status().isOk());
    }
}
