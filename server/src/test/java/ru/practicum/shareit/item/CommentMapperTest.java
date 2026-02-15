package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CommentMapperTest {

    @Test
    void toCommentDtoTest() {
        User author = User.builder()
                .id(10L)
                .name("John")
                .email("john@example.com")
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Nice item")
                .author(author)
                .created(LocalDateTime.of(2024, 1, 1, 10, 0))
                .build();

        CommentDto dto = CommentMapper.toCommentDto(comment);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getText()).isEqualTo("Nice item");
        assertThat(dto.getAuthorName()).isEqualTo("John");
        assertThat(dto.getCreated()).isEqualTo(comment.getCreated());
    }

    @Test
    void toCommentTest() {
        CommentDto dto = CommentDto.builder()
                .id(2L)
                .text("Great!")
                .created(LocalDateTime.of(2024, 3, 10, 15, 30))
                .build();

        Comment comment = CommentMapper.toComment(dto);

        assertThat(comment.getId()).isEqualTo(2L);
        assertThat(comment.getText()).isEqualTo("Great!");
        assertThat(comment.getCreated()).isEqualTo(dto.getCreated());
    }

    @Test
    void toCommentIfCreatedIsNullTest() {
        CommentDto dto = CommentDto.builder()
                .id(3L)
                .text("Cool")
                .created(null)
                .build();

        LocalDateTime before = LocalDateTime.now();
        Comment comment = CommentMapper.toComment(dto);
        LocalDateTime after = LocalDateTime.now();

        assertThat(comment.getId()).isEqualTo(3L);
        assertThat(comment.getText()).isEqualTo("Cool");

        assertThat(comment.getCreated()).isNotNull();
        assertThat(comment.getCreated()).isAfterOrEqualTo(before);
        assertThat(comment.getCreated()).isBeforeOrEqualTo(after);
    }
}

