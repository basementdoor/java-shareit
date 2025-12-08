package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constants.USER_ID_HEADER;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private static final String ITEM_PREFIX = "/items";
    private static final Long ITEM_ID = 1L;
    private static final Long USER_ID = 1L;
    private ItemDto requestItemDto;
    private UpdateItemDto updateItemDto;
    private ItemDtoWithBookingsAndComments responseItemDto;
    
    @BeforeEach
    public void setUp() {
        requestItemDto = new ItemDto(null, "Thing", "Such a great thing", true, null, null);
        updateItemDto = new UpdateItemDto("Updated thing", "It's even better!", true);

        responseItemDto = new ItemDtoWithBookingsAndComments(ITEM_ID, "Thing", "Such a great thing", true,
        null, null, null, null);
    }

    @Test
    void createItemTest() throws Exception {
        ItemDto createdItem = requestItemDto;
        createdItem.setId(ITEM_ID);
        when(itemService.create(anyLong(), any())).thenReturn(createdItem);

        mockMvc.perform(post(ITEM_PREFIX)
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdItem)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Thing"))
                .andExpect(jsonPath("$.id").value(ITEM_ID));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updatedItem = new ItemDto(
                ITEM_ID,
                "Updated thing",
                "It's even better!",
                true,
                null,
                null
        );

        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(updatedItem);

        mockMvc.perform(patch(ITEM_PREFIX + "/" + ITEM_ID)
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated thing"))
                .andExpect(jsonPath("$.id").value(ITEM_ID));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(responseItemDto);

        mockMvc.perform(get(ITEM_PREFIX + "/" + ITEM_ID)
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ITEM_ID));
    }

    @Test
    void getItemsByUserTest() throws Exception {
        when(itemService.getAllByUser(anyLong())).thenReturn(List.of(responseItemDto));

        mockMvc.perform(get(ITEM_PREFIX)
                        .header(USER_ID_HEADER, USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto searchResponse = requestItemDto;
        searchResponse.setId(ITEM_ID);

        when(itemService.search(anyString())).thenReturn(List.of(searchResponse));

        mockMvc.perform(get(ITEM_PREFIX + "/search")
                        .param("text", "thing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(ITEM_ID));
    }

    @Test
    void addCommentTest() throws Exception {
        CommentDto requestComment = new CommentDto(null, "Nice item!", "Steve", LocalDateTime.now());
        CommentDto responseComment = requestComment;
        responseComment.setId(1L);

        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(responseComment);

        mockMvc.perform(post(ITEM_PREFIX + "/" + ITEM_ID + "/comment")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}