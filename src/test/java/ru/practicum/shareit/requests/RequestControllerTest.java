package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {
    @MockBean
    RequestService requestService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Autowired
    public RequestControllerTest(RequestService requestService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.requestService = requestService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    static ItemRequestDto itemRequestDto;
    static ItemRequest itemRequest;

    @BeforeEach
    public void init() {
        itemRequestDto = new ItemRequestDto("Table", 1L, LocalDateTime.now());

        itemRequest = new ItemRequest();
        itemRequest.setId(3L);
        itemRequest.setDescription("Table");
        itemRequest.setUserRequesterId(2L);
        itemRequest.setCreated(LocalDateTime.now());
    }

    @Test
    public void createRequest_thenStatus200andRequestReturned() throws Exception {
        Mockito.when(requestService.save(Mockito.any(), Mockito.anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(
                        post("/requests")
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()));
    }

    @Test
    public void getAllUserRequest_thenStatus200() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDto);

        Mockito.when(requestService.getAllUserRequest(Mockito.anyLong()))
                .thenReturn(itemRequestDtoList);
        mockMvc.perform(
                        get("/requests")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getAllRequest_thenStatus200() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDto);

        Mockito.when(requestService.findAllRequest(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(itemRequestDtoList);
        String from = "0";
        String size = "1";
        mockMvc.perform(
                        get("/requests/all")
                                .accept(MediaType.APPLICATION_JSON)
                                .param("from", from)
                                .param("size", size)
                                .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk());
    }

    @Test
    public void getRequest_thenStatus200() throws Exception {
        Mockito.when(requestService.getRequest(Mockito.anyLong()))
                .thenReturn(itemRequestDto);
        mockMvc.perform(
                        get("/requests/{requestId}", itemRequestDto.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(itemRequest.getDescription()));
    }
}
