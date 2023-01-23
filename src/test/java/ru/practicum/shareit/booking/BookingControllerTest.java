package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    BookingService bookingService;

    OutBookingDto returnedBookingDto = new OutBookingDto(
            1, null, null, null, null, Status.WAITING);
    private final User user = new User(1L, "User1", "user1@mail.ru");
    private final Item item = new Item(1L, "Item1", "item1_desc", true, user, null);
    private final Booking bookingDto = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), item, user, Status.WAITING);
    private final Booking bookingDto2 = new Booking(1L, LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(5), item, user, Status.WAITING);
    private final OutBookingDto booking1 = BookingMapper.toBookingDto(bookingDto);
    private final OutBookingDto booking2 = BookingMapper.toBookingDto(bookingDto2);
    @Test
    void create() throws Exception {
        when(bookingService.createBooking(anyLong(), any()))
                .thenReturn(returnedBookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(returnedBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(returnedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(returnedBookingDto.getStatus().name())));
    }

    @Test
    void update() throws Exception {
        when(bookingService.updateBookingStatus(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(returnedBookingDto);
        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(returnedBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(returnedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(returnedBookingDto.getStatus().name())));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(returnedBookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(returnedBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(returnedBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(returnedBookingDto.getStatus().name())));
    }
    @Test
    public void getAllBooking() {
        when(bookingService.getAllBookingByUser(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(booking1, booking2));
        try {
            mvc.perform(get("/bookings")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .param("from", "0")
                            .param("state", "ALL")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.*", is(hasSize(2))))
                    .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    public void getAllBookingByOwner() {
        when(bookingService.getAllBookingByOwner(Mockito.anyLong(), Mockito.any(State.class), Mockito.anyInt(),
                Mockito.anyInt()))
                .thenReturn(List.of(booking1, booking2));
        try {
            mvc.perform(get("/bookings/owner")
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("X-Sharer-User-Id", 1)
                            .param("from", "0")
                            .param("state", "ALL")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.*", is(hasSize(2))))
                    .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}