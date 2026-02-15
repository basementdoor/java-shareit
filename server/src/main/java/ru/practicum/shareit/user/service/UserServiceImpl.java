package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(userId)));
    }

    @Override
    @Transactional
    public UserDto create(UserDto user) {
        User newUser = UserMapper.toUser(user);
        throwIfEmailTaken(newUser);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    @Transactional
    public UserDto update(UpdateUserDto user) {
        User updateUser = UserMapper.toUser(user);
        User existUser = throwIfUserNotExist(updateUser.getId());
        throwIfEmailTaken(updateUser);
        if (updateUser.getName() != null) existUser.setName(updateUser.getName());
        if (updateUser.getEmail() != null) existUser.setEmail(updateUser.getEmail());
        return UserMapper.toUserDto(userRepository.save(existUser));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private User throwIfUserNotExist(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }

    private void throwIfEmailTaken(User user) {
        Optional<User> userWithEmail = userRepository.findByEmail(user.getEmail());
        if (userWithEmail.isPresent()) {
            if (!Objects.equals(user.getId(), userWithEmail.get().getId())) {
                throw new DuplicateValidationException("Пользователь с email: '%s' уже существует"
                        .formatted(user.getEmail()));
            }
        }
    }
}
