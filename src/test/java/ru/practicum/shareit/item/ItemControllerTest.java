package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    ItemService itemService;

    ItemDtoWithBooking itemDto = new ItemDtoWithBooking(1L, "item1", "item1_desc", true, null, null,null);
    List<ItemDtoWithBooking> itemDtos = List.of(
            new ItemDtoWithBooking(1L, "item1", "item1_desc", true, null, null, null),
            new ItemDtoWithBooking(2L, "item2", "item2_desc", true, null, null, null));
    List<ItemDto> searchItemDtos = List.of(new ItemDto(1, "item1", "item1_desc", true, null));
    CommentDto commentDto = new CommentDto(1L, "comment-item1", "user2",
            null);

    @Test
    void create() throws Exception {
        when(itemService.createItem(any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void update() throws Exception {
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void getAll() throws Exception {
        when(itemService.getAllItem(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemDtos);
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("item1", "item2")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(
                        "item1_desc", "item2_desc")));
    }

    @Test
    void search() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(searchItemDtos);
        mvc.perform(get("/items/search?text=item1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(searchItemDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("item1")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("item1_desc")));
    }

    @Test
    void addComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}