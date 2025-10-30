package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.DuplicateValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User create(User user) {
        user.setId(generateNextId());
        throwIfEmailTaken(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User userUpdate) {
        User user = throwIfUserNotExist(userUpdate.getId());
        throwIfEmailTaken(userUpdate);
        if (userUpdate.getName() != null) user.setName(userUpdate.getName());
        if (userUpdate.getEmail() != null) user.setEmail(userUpdate.getEmail());
        return user;
    }

    @Override
    public void delete(Long id) {
        throwIfUserNotExist(id);
        users.remove(id);
    }

    private Long generateNextId() {
        Long nextId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(0L);
        return ++nextId;
    }

    private void throwIfEmailTaken(User user) {
        boolean isEmailTaken = users.values().stream()
                .filter(existedUser -> !Objects.equals(existedUser.getId(), user.getId()))
                .anyMatch(existedUser -> Objects.equals(existedUser.getEmail(), user.getEmail()));
        if (isEmailTaken) {
            throw new DuplicateValidationException("Пользователь с email: '%s' уже существует"
                    .formatted(user.getEmail()));
        }
    }

    private User throwIfUserNotExist(Long id) {
        return getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: %s не найден".formatted(id)));
    }
}
