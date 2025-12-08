package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    UserDto userDto;
    UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("User-1")
                .email("user@gmail.com")
                .build();

        updateUserDto = UpdateUserDto.builder()
                .name("Updated")
                .email("updated@gmail.com")
                .build();
    }

    @Test
    void createUserSuccessTest() {
        UserDto saved = userService.create(userDto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(userDto.getName());
        assertThat(saved.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void createThrowIfEmailNotUniqueTest() {
        userRepository.save(new User(null, "Another", userDto.getEmail()));

        assertThatThrownBy(() -> userService.create(userDto))
                .isInstanceOf(DuplicateValidationException.class)
                .hasMessageContaining("уже существует");
    }

    @Test
    void getUserByIdTest() {
        User saved = userRepository.save(new User(null, userDto.getName(), userDto.getEmail()));

        UserDto found = userService.getById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo(saved.getName());
        assertThat(found.getEmail()).isEqualTo(saved.getEmail());
    }

    @Test
    void getUserByIdThrowIfUserNotFoundTest() {
        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserNameAndEmailTest() {
        Long id = userRepository.save(new User(null, "Old", "old@mail.com")).getId();

        updateUserDto.setId(id);

        UserDto updated = userService.update(updateUserDto);

        assertThat(updated.getName()).isEqualTo(updateUserDto.getName());
        assertThat(updated.getEmail()).isEqualTo(updateUserDto.getEmail());
    }

    @Test
    void updateOnlyEmailIfNameNullTest() {
        Long id = userRepository.save(new User(null, "Old", "old@mail.com")).getId();

        updateUserDto.setId(id);
        updateUserDto.setName(null);

        UserDto updated = userService.update(updateUserDto);

        assertThat(updated.getName()).isEqualTo("Old");
        assertThat(updated.getEmail()).isEqualTo(updateUserDto.getEmail());
    }

    @Test
    void updateOnlyNameIfEmailNullTest() {
        Long id = userRepository.save(new User(null, "Old", "old@mail.com")).getId();

        updateUserDto.setId(id);
        updateUserDto.setEmail(null);

        UserDto updated = userService.update(updateUserDto);

        assertThat(updated.getName()).isEqualTo(updateUserDto.getName());
        assertThat(updated.getEmail()).isEqualTo("old@mail.com");
    }

    @Test
    void updateThrowIfEmailNotUniqueTest() {
        User u1 = userRepository.save(new User(null, "U1", "mail1@test.com"));
        User u2 = userRepository.save(new User(null, "U2", "mail2@test.com"));

        updateUserDto.setId(u2.getId());
        updateUserDto.setEmail(u1.getEmail()); // пытаемся поставить email другого пользователя

        assertThatThrownBy(() -> userService.update(updateUserDto))
                .isInstanceOf(DuplicateValidationException.class);
    }

    @Test
    void updateThrowIfUserNotFoundTest() {
        updateUserDto.setId(999L);

        assertThatThrownBy(() -> userService.update(updateUserDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteUserTest() {
        Long id = userRepository.save(new User(null, "Name", "mail@test.com")).getId();

        assertDoesNotThrow(() -> userService.delete(id));

        assertFalse(userRepository.findById(id).isPresent());
    }

    @Test
    void deleteNotThrowIfUserNotExistTest() {
        assertDoesNotThrow(() -> userService.delete(999L));
    }

    @Test
    void getAllUsersTest() {
        userRepository.save(new User(null, "U1", "u1@test.com"));
        userRepository.save(new User(null, "U2", "u2@test.com"));

        Collection<UserDto> users = userService.getAll();

        assertThat(users).hasSize(2);
    }

    @Test
    void getAllReturnEmptyListTest() {
        Collection<UserDto> users = userService.getAll();

        assertThat(users).isEmpty();
    }
}
