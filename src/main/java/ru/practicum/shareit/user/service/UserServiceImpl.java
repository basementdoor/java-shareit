package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getById(Long userId) {
        return userRepository.getById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(userId)));
    }

    @Override
    public UserDto create(UserDto user) {
        User newUser = UserMapper.toUser(user);
        newUser = userRepository.create(newUser);
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public UserDto update(UpdateUserDto user) {
        User updatedUser = UserMapper.toUser(user);
        updatedUser = userRepository.update(updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }
}
