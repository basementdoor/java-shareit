package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UpdateUserDto updateUserDto;

    private static final Long USER_ID = 1L;
    private static final String USERS_PREFIX = "/users";

    @BeforeEach
    void setup() {
        userDto = UserDto.builder()
                .id(USER_ID)
                .name("John")
                .email("john@example.com")
                .build();

        updateUserDto = UpdateUserDto.builder()
                .id(USER_ID)
                .name("Updated")
                .email("upd@example.com")
                .build();
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get(USERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USER_ID))
                .andExpect(jsonPath("$[0].name").value("John"));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get(USERS_PREFIX + "/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.create(any())).thenReturn(userDto);

        mockMvc.perform(post(USERS_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.update(any())).thenReturn(userDto);

        mockMvc.perform(patch(USERS_PREFIX + "/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(USER_ID))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete(USERS_PREFIX + "/" + USER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(USER_ID);
    }
}
