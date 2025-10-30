package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {

    Collection<User> getAll();

    Optional<User> getById(Long userId);

    User create(User user);

    User update(User user);

    void delete(Long id);
}
