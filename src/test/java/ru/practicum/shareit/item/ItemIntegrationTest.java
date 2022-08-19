package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemRepository repository;
    @Autowired
    private UserRepository repository2;
    @Autowired
    private RequestRepository repository3;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void resetDb() {
        repository.deleteAll();
        repository3.deleteAll();
        repository2.deleteAll();
    }

    @Test
    public void createItem_thenStatus200andItemReturned() throws Exception {
        User owner = new User();
        owner.setName("Ivan");
        owner.setEmail("email@com.ru");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Table");
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setName("Table");
        item.setDescription("Big table");
        item.setAvailable(true);

        MvcResult result = mockMvc.perform(
                        post("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(owner))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(owner.getName()))
                .andReturn();

        Integer userId = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

        item.setOwner(userId.longValue());
        itemRequest.setUserRequesterId(userId.longValue());

        MvcResult result3 = mockMvc.perform(
                        post("/requests")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()))
                .andReturn();

        Integer requestId = JsonPath.read(result3.getResponse().getContentAsString(), "$.id");
        item.setRequest(requestId.longValue());

        MvcResult result2 = mockMvc.perform(
                        post("/items")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        Integer itemId = JsonPath.read(result2.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(
                        get("/items/{id}", itemId)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value(item.getName()));
    }
}
