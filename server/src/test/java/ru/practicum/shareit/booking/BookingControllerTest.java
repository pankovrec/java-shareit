package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    BookingService bookingService;
    @InjectMocks
    private BookingController controller;
    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    private final OutBookingDto returnedBookingDto = new OutBookingDto(
            1, null, null, null, null, Status.WAITING);
    private final OutBookingDto returnedBookingDto2 = new OutBookingDto(
            99, null, null, null, null, Status.WAITING);
    private final User user = new User(1L, "User1", "user1@mail.ru");
    private final Item item = new Item(1L, "Item1", "item1_desc", true, user, null);
    private final Booking bookingDto = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), item, user, Status.WAITING);
    private final Booking bookingDto2 = new Booking(1L, LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(5), item, user, Status.WAITING);
    private final OutBookingDto booking1 = BookingMapper.toBookingDto(bookingDto);
    private final OutBookingDto booking2 = BookingMapper.toBookingDto(bookingDto2);

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

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
}