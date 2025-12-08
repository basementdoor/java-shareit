package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestService;
    private static final String REQUEST_PREFIX = "/requests";
    private static final Long REQUEST_ID = 1L;
    private static final Long USER_ID = 1L;
    private ItemRequestDto request;
    private ItemRequestDto response;

    @BeforeEach
    public void setUp() {
        request = ItemRequestDto.builder()
                .id(null)
                .description("I need your jacket")
                .requesterId(USER_ID)
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        response = request;
        response.setId(REQUEST_ID);
    }

    @Test
    void createRequestTest() throws Exception {
        when(requestService.create(anyLong(), any())).thenReturn(response);

        mockMvc.perform(post(REQUEST_PREFIX)
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("I need your jacket"));
    }

    @Test
    void getByUserTest() throws Exception {
        when(requestService.getByUser(anyLong())).thenReturn(List.of(response));

        mockMvc.perform(get(REQUEST_PREFIX)
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID));
    }

    @Test
    void getAllTest() throws Exception {
        when(requestService.getAll(anyLong())).thenReturn(List.of(response));

        mockMvc.perform(get(REQUEST_PREFIX + "/all")
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(REQUEST_ID));
    }

    @Test
    void getByIdTest() throws Exception {
        when(requestService.getById(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get(REQUEST_PREFIX + "/" + REQUEST_ID)
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(REQUEST_ID))
                .andExpect(jsonPath("$.description").value("I need your jacket"));
    }
}
