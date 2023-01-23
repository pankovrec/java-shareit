package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.OutBookingDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.when;

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