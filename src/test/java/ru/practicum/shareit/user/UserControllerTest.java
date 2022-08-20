package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public UserControllerTest(UserService userService, MockMvc mockMvc,
                              ObjectMapper objectMapper) {
        this.userService = userService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    static User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setName("Ivan");
        user.setEmail("email@com.ru");
        user.setId(10L);
    }

    @Test
    public void createUser_thenStatus200andPersonReturned() throws Exception {
        Mockito.when(userService.save(Mockito.any())).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void createUserWithException() throws Exception {
        User badUser = new User();
        badUser.setEmail("@example.com");
        badUser.setName("test");
        Mockito.when(userService.save(Mockito.any())).thenReturn(badUser);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        post("/users")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(badUser))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertEquals(result.getResolvedException().getClass(), MethodArgumentNotValidException.class));
    }

    @Test
    public void getUnknownUser() throws Exception {
        Mockito.when(userService.findById(Mockito.any())).thenThrow(UserNotFoundException.class);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        get("/users/{id}", 123)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void upDateUser_thenStatus200andPersonReturned() throws Exception {
        Mockito.when(userService.upDate(Mockito.any(), eq(user.getId()))).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        patch("/users/{id}", user.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void getUser_thenStatus200andPersonReturned() throws Exception {
        Mockito.when(userService.findById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        get("/users/{id}", user.getId())
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    public void getAllUser_thenStatus200andPersonReturned() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(user);

        Mockito.when(userService.findAll()).thenReturn(users);
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();
        Mockito.when(userMapper.toUserDtoList(Mockito.any())).thenCallRealMethod();
        mockMvc.perform(
                        get("/users")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void deleteByIdUser_thenStatus200andPersonReturned() throws Exception {
        Mockito.when(userMapper.toUserDto(Mockito.any())).thenCallRealMethod();

        mockMvc.perform(
                        delete("/users/{id}", user.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteById(user.getId());
    }
}