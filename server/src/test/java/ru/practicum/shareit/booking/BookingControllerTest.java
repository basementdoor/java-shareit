package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private static final String BOOKING_PREFIX = "/bookings";
    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long BOOKING_ID = 1L;
    private BookingDto responseBookingDto;


    @BeforeEach
    public void setUp() {
        RequestBookingDto requestBookingDto = new RequestBookingDto(ITEM_ID,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        responseBookingDto = new BookingDto(BOOKING_ID, requestBookingDto.getStart(), requestBookingDto.getEnd(),
                null, null, BookingStatus.WAITING);
    }

    @Test
    public void createBookingTest() throws Exception {
        when(bookingService.create(anyLong(), any())).thenReturn(responseBookingDto);

        mockMvc.perform(post(BOOKING_PREFIX)
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseBookingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(BOOKING_ID));
    }

    @Test
    public void updateStatusTest() throws Exception {
        responseBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateStatus(anyLong(), any(), anyBoolean())).thenReturn(responseBookingDto);

        mockMvc.perform(patch(BOOKING_PREFIX + "/" + BOOKING_ID)
                        .header(USER_ID_HEADER, USER_ID)
                        .param("approved", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void getBookingTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(responseBookingDto);

        mockMvc.perform(get(BOOKING_PREFIX + "/" + BOOKING_ID)
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseBookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(BOOKING_ID));
    }

    @Test
    public void getUserBookings() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any())).thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get(BOOKING_PREFIX)
                        .header(USER_ID_HEADER, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID));
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any())).thenReturn(List.of(responseBookingDto));

        mockMvc.perform(get(BOOKING_PREFIX + "/owner")
                        .header(USER_ID_HEADER, USER_ID)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(BOOKING_ID));
    }
}
