package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperForOwner;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserForUpdate;
import ru.practicum.shareit.user.service.UserService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserControllerTest {
    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    @MockBean
    private ItemMapperForOwner itemMapperForOwner;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private BookingMapper bookingMapper;

    @MockBean
    private RequestService requestService;

    @MockBean
    private UserMapper userMapper;

    private UserService repository;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, UserService repository) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.repository = repository;
    }
    static User user;
    @BeforeEach
    public void init() {
        user = new User();
        user.setName("Ivan");
        user.setEmail("email@com.ru");
        user.setId(10L);

//        updatedUser = new UserForUpdate();
//        updatedUser.setName("Maxim");
//        updatedUser.setEmail("email123@com.ru");
//        updatedUser.setId(10L);
//
//        badUser = new User();
//        badUser.setName("Maxim");
//        badUser.setEmail("emailcom.ru");
//        badUser.setId(11L);
    }

//    @Test
//    public void givenPerson_whenAdd_thenStatus201andPersonReturned() throws Exception {
//        Mockito.when(repository.save(Mockito.any())).thenReturn(user);
//        mockMvc.perform(
//                        post("/users")
//                                .content(objectMapper.writeValueAsString(user))
//                                .contentType(MediaType.APPLICATION_JSON)
//                )
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.id").isNumber());
//    }
}
