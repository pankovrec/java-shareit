package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService requestService;
    @InjectMocks
    private ItemRequestController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private final User user2 = new User(2L, "user2", "user2@email.com");
    private final User user3 = new User(3L, "user3", "user3@email.com");
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "I wanna use item1",
            user2, null, null);
    private final List<ItemRequestDto> itemRequestDtos = List.of(
            new ItemRequestDto(1, "I wanna use item1", user2, LocalDateTime.now(), null),
            new ItemRequestDto(2, "I wanna use item2", user3, LocalDateTime.now(), null));

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void create() throws Exception {
        when(requestService.createRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class));
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requester.id", is(itemRequestDto.getRequester().getId()), Long.class));
    }
}