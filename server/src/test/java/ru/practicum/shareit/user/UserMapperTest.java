package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    @Test
    void toUserDtoTest() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("John Doe");
        assertThat(dto.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void toUserFromUserDtoTest() {
        UserDto dto = UserDto.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getName()).isEqualTo("Jane Doe");
        assertThat(user.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void toUserFromUpdateUserDtoTest() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(3L)
                .name("Mike")
                .email("mike@example.com")
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(3L);
        assertThat(user.getName()).isEqualTo("Mike");
        assertThat(user.getEmail()).isEqualTo("mike@example.com");
    }

    @Test
    void toUserFromUpdateUserDtoNameNullTest() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(4L)
                .name(null)
                .email("test@example.com")
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(4L);
        assertThat(user.getName()).isNull();
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void toUserFromUpdateUserDtoEmailNullTest() {
        UpdateUserDto dto = UpdateUserDto.builder()
                .id(5L)
                .name("Alice")
                .email(null)
                .build();

        User user = UserMapper.toUser(dto);

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getName()).isEqualTo("Alice");
        assertThat(user.getEmail()).isNull();
    }
}
